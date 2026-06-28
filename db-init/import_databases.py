import os
import subprocess
import sys

# Configuration
MYSQL_PATH = r"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
MYSQL_USER = "root"
MYSQL_PASS = "Emma19"
DB_INIT_DIR = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"

# SQL files in logical import order
SQL_FILES = [
    "user_service.sql",
    "terapeuti_service.sql",
    "servicii_service.sql",
    "pacienti_service.sql",
    "programari_service.sql",
    "chat_service.sql",
    "notificari_service.sql"
]

def check_mysql():
    if not os.path.exists(MYSQL_PATH):
        print(f"Error: MySQL client not found at '{MYSQL_PATH}'.")
        sys.exit(1)
    print("MySQL client verified.")

def import_sql_file(file_name):
    file_path = os.path.join(DB_INIT_DIR, file_name)
    if not os.path.exists(file_path):
        print(f"Error: SQL file not found: {file_path}")
        return False
    
    # Format path for MySQL source command (forward slashes are safer under Windows MySQL)
    safe_path = file_path.replace("\\", "/")
    print(f"Importing {file_name}...")
    
    # Build command: mysql.exe -u root -pPassword --default-character-set=utf8mb4 -e "source <path>"
    cmd = [
        MYSQL_PATH,
        f"-u{MYSQL_USER}",
        f"-p{MYSQL_PASS}",
        "--default-character-set=utf8mb4",
        "-e", f"source {safe_path}"
    ]
    
    try:
        # Run process
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        print(f"Successfully imported {file_name}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error importing {file_name}:")
        print(e.stderr)
        return False

def verify_diacritics():
    print("\nVerifying diacritics in database...")
    query = "SELECT id, diagnostic, observatii FROM programari_service.evaluari WHERE id = 11;"
    cmd = [
        MYSQL_PATH,
        f"-u{MYSQL_USER}",
        f"-p{MYSQL_PASS}",
        "--default-character-set=utf8mb4",
        "-e", query
    ]
    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        print("Query Output:")
        print(result.stdout)
        
        # Check if the text contains "??"
        if "??" in result.stdout:
            print("WARNING: Diacritics are still showing as '??'!")
        else:
            print("SUCCESS: Diacritics verified! No '??' found in the test record.")
    except Exception as e:
        print(f"Failed to run verification query: {e}")

def main():
    check_mysql()
    
    success_count = 0
    for sql_file in SQL_FILES:
        if import_sql_file(sql_file):
            success_count += 1
            
    print(f"\nImport finished: {success_count}/{len(SQL_FILES)} databases imported successfully.")
    
    if success_count == len(SQL_FILES):
        verify_diacritics()
    else:
        print("Some imports failed. Verification skipped.")

if __name__ == "__main__":
    main()
