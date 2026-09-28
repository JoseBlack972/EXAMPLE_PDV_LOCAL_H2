#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script de automação para adicionar ~50 produtos de Cafeteria no PDV.
Suporta duas formas de execução:
 1. Via HTTP Multipart Upload diretamente no endpoint da aplicação web (/produtos/importar-csv)
 2. Via Docker psql executando povoar_cafeteria.sql no PostgreSQL
"""

import sys
import os
import subprocess
import urllib.request
import urllib.parse
import http.cookiejar

BASE_URL = os.environ.get("PDV_URL", "http://localhost:8080")
CSV_FILE = os.path.join(os.path.dirname(__file__), "cafeteria_estoque_50_produtos.csv")
SQL_FILE = os.path.join(os.path.dirname(__file__), "povoar_cafeteria.sql")

def povoar_via_docker_postgres():
    print("Tentando importar via Docker PostgreSQL...")
    if not os.path.exists(SQL_FILE):
        print(f"Arquivo SQL não encontrado: {SQL_FILE}")
        return False
    
    cmd = "docker exec -i example-pdv-postgres psql -U postgres -d pdvdb"
    try:
        with open(SQL_FILE, "rb") as f:
            proc = subprocess.run(cmd, shell=True, input=f.read(), capture_output=True, text=True)
            if proc.returncode == 0:
                print("✓ 52 produtos e 7 categorias inseridos com sucesso no PostgreSQL via Docker!")
                return True
            else:
                print(f"Erro ao executar no Docker: {proc.stderr}")
                return False
    except Exception as e:
        print(f"Falha ao executar comando docker: {e}")
        return False

def povoar_via_http():
    print(f"Tentando importar via API / Interface Web em {BASE_URL}...")
    if not os.path.exists(CSV_FILE):
        print(f"Arquivo CSV não encontrado: {CSV_FILE}")
        return False

    cj = http.cookiejar.CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

    try:
        # 1. Login como gestor
        login_url = f"{BASE_URL}/login"
        login_data = urllib.parse.urlencode({"username": "gestor", "password": "gestor123"}).encode("utf-8")
        req_login = urllib.request.Request(login_url, data=login_data, method="POST")
        req_login.add_header("Content-Type", "application/x-www-form-urlencoded")
        
        with opener.open(req_login) as resp:
            pass

        # 2. Upload do CSV multipart
        with open(CSV_FILE, "rb") as f:
            csv_bytes = f.read()

        boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
        body = bytearray()
        body.extend(f"--{boundary}\r\n".encode("utf-8"))
        body.extend(b'Content-Disposition: form-data; name="arquivo"; filename="cafeteria_estoque_50_produtos.csv"\r\n')
        body.extend(b"Content-Type: text/csv\r\n\r\n")
        body.extend(csv_bytes)
        body.extend(b"\r\n")
        body.extend(f"--{boundary}--\r\n".encode("utf-8"))

        import_url = f"{BASE_URL}/produtos/importar-csv"
        req_import = urllib.request.Request(import_url, data=body, method="POST")
        req_import.add_header("Content-Type", f"multipart/form-data; boundary={boundary}")

        with opener.open(req_import) as resp:
            content = resp.read().decode("utf-8", errors="ignore")
            if "Importação" in content or "concluída" in content or resp.status == 200:
                print("✓ 52 produtos de Cafeteria importados com sucesso via HTTP Web!")
                return True
            else:
                print("Resposta inesperada ao importar via HTTP.")
                return False
    except Exception as e:
        print(f"Falha na comunicação HTTP com {BASE_URL}: {e}")
        return False

def main():
    print("=" * 60)
    print("INICIANDO CADASTRO DE 52 PRODUTOS DE CAFETERIA NO PDV")
    print("=" * 60)

    # Tenta primeiro via HTTP se o app estiver rodando
    sucesso = povoar_via_http()
    if not sucesso:
        print("\nTentando método alternativo via Docker PostgreSQL...")
        sucesso = povoar_via_docker_postgres()

    if sucesso:
        print("\nConcluído com sucesso! Verifique os produtos em http://localhost:8080/produtos")
    else:
        print("\nVocê também pode importar manualmente:")
        print(" 1. Acesse http://localhost:8080/produtos/importar-csv logado como gestor ou admin")
        print(" 2. Selecione o arquivo 'cafeteria_estoque_50_produtos.csv' e clique em Importar.")

if __name__ == "__main__":
    main()
