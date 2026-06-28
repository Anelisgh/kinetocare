import os
import re

DB_INIT_DIR = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"

def parse_columns_from_create_table(sql_content):
    # Map table_name -> list of column_names
    table_columns = {}
    
    # Regex to find CREATE TABLE blocks
    # It matches: CREATE TABLE `table_name` ( ... ) ENGINE=
    create_table_regex = re.compile(
        r"CREATE TABLE\s+`(\w+)`\s*\((.*?)\)\s*ENGINE\s*=", 
        re.DOTALL | re.IGNORECASE
    )
    
    for match in create_table_regex.finditer(sql_content):
        table_name = match.group(1)
        body = match.group(2)
        
        columns = []
        # Parse lines in the body of CREATE TABLE
        for line in body.split("\n"):
            line = line.strip()
            if not line:
                continue
            
            # Skip keys, constraints, etc.
            upper_line = line.upper()
            if any(upper_line.startswith(x) for x in ["KEY ", "PRIMARY KEY", "UNIQUE KEY", "CONSTRAINT", "FOREIGN KEY"]):
                continue
                
            # Column name is enclosed in backticks at the start of the line
            col_match = re.match(r"^`(\w+)`", line)
            if col_match:
                columns.append(col_match.group(1))
                
        if columns:
            table_columns[table_name] = columns
            print(f"  Parsed table '{table_name}' with {len(columns)} columns: {', '.join(columns)}")
            
    return table_columns

def add_column_lists_to_inserts(file_path):
    print(f"Processing {os.path.basename(file_path)}...")
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    table_columns = parse_columns_from_create_table(content)
    if not table_columns:
        print(f"  No tables found in {os.path.basename(file_path)}.")
        return
        
    modified = False
    
    # We want to find: INSERT INTO `table_name` VALUES
    # and replace with: INSERT INTO `table_name` (`col1`, `col2`, ...) VALUES
    # Only replace if it doesn't already have a column list (i.e. no parenthesis after table name)
    def replacer(match):
        nonlocal modified
        table_name = match.group(1)
        if table_name in table_columns:
            cols = table_columns[table_name]
            cols_str = ", ".join(f"`{c}`" for c in cols)
            modified = True
            return f"INSERT INTO `{table_name}` ({cols_str}) VALUES"
        return match.group(0)
        
    # Regex to match: INSERT INTO `table_name` VALUES
    # Allowing potential space/newlines
    insert_regex = re.compile(r"INSERT INTO\s+`(\w+)`\s+VALUES", re.IGNORECASE)
    
    new_content = insert_regex.sub(replacer, content)
    
    if modified:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"  Successfully updated INSERT statements in {os.path.basename(file_path)}.")
    else:
        print(f"  No updates needed for {os.path.basename(file_path)}.")

def main():
    sql_files = [f for f in os.listdir(DB_INIT_DIR) if f.endswith(".sql") and f != "mock-data-june-july.sql"]
    print(f"Found {len(sql_files)} SQL files to process.")
    for sql_file in sql_files:
        file_path = os.path.join(DB_INIT_DIR, sql_file)
        add_column_lists_to_inserts(file_path)

if __name__ == "__main__":
    main()
