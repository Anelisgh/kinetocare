import os
import re

DB_INIT_DIR = r"c:\Users\Aneliss\Desktop\kinetocare\db-init"

def parse_columns_from_create_table(sql_content):
    table_columns = {}
    create_table_regex = re.compile(
        r"CREATE TABLE\s+`(\w+)`\s*\((.*?)\)\s*ENGINE\s*=", 
        re.DOTALL | re.IGNORECASE
    )
    
    for match in create_table_regex.finditer(sql_content):
        table_name = match.group(1)
        body = match.group(2)
        
        columns = []
        for line in body.split("\n"):
            line = line.strip()
            if not line:
                continue
            
            upper_line = line.upper()
            if any(upper_line.startswith(x) for x in ["KEY ", "PRIMARY KEY", "UNIQUE KEY", "CONSTRAINT", "FOREIGN KEY"]):
                continue
                
            col_match = re.match(r"^`(\w+)`", line)
            if col_match:
                columns.append(col_match.group(1))
                
        if columns:
            table_columns[table_name] = columns
            
    return table_columns

def get_first_row_and_count_values(sql_content, insert_pos):
    # Search for the first non-comment, non-empty line after insert_pos
    start_idx = -1
    remaining = sql_content[insert_pos:]
    lines = remaining.split("\n")
    
    offset = 0
    for line in lines:
        stripped = line.strip()
        # Skip empty lines and SQL comments
        if not stripped or stripped.startswith("--") or stripped.startswith("/*") or stripped.startswith("#"):
            offset += len(line) + 1 # +1 for the newline
            continue
            
        # Found the first real line, find the first '(' on it
        paren_in_line = line.find("(")
        if paren_in_line != -1:
            start_idx = insert_pos + offset + paren_in_line
            break
        else:
            offset += len(line) + 1
            
    if start_idx == -1:
        return 0
        
    # Parse from start_idx to find the matching ')'
    in_string = False
    escape = False
    paren_depth = 0
    commas = 0
    
    for idx in range(start_idx, len(sql_content)):
        char = sql_content[idx]
        
        if escape:
            escape = False
            continue
        if char == '\\':
            escape = True
            continue
        if char == "'":
            in_string = not in_string
            continue
            
        if not in_string:
            if char == '(':
                paren_depth += 1
            elif char == ')':
                paren_depth -= 1
                if paren_depth == 0:
                    return commas + 1
            elif char == ',' and paren_depth == 1:
                commas += 1
                
    return 0

def fix_sql_file(file_path):
    print(f"Processing {os.path.basename(file_path)}...")
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    table_columns = parse_columns_from_create_table(content)
    if not table_columns:
        print(f"  No tables found in {os.path.basename(file_path)}.")
        return
        
    insert_pattern = re.compile(r"INSERT INTO\s+`(\w+)`(?:\s*\([^)]*\))?\s+VALUES", re.IGNORECASE)
    
    new_content = ""
    last_idx = 0
    modified = False
    
    for match in insert_pattern.finditer(content):
        table_name = match.group(1)
        start_pos = match.start()
        end_pos = match.end()
        
        new_content += content[last_idx:start_pos]
        
        num_values = get_first_row_and_count_values(content, end_pos)
        
        if table_name in table_columns and num_values > 0:
            all_cols = table_columns[table_name]
            sliced_cols = all_cols[:num_values]
            
            if num_values > len(all_cols):
                print(f"  WARNING: Table '{table_name}' has only {len(all_cols)} columns, but insert has {num_values} values!")
                sliced_cols = all_cols
                
            cols_str = ", ".join(f"`{c}`" for c in sliced_cols)
            replacement = f"INSERT INTO `{table_name}` ({cols_str}) VALUES"
            new_content += replacement
            modified = True
            print(f"  Updated '{table_name}': mapped {num_values}/{len(all_cols)} columns.")
        else:
            new_content += match.group(0)
            
        last_idx = end_pos
        
    new_content += content[last_idx:]
    
    if modified:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"  Successfully updated {os.path.basename(file_path)}.")
    else:
        print(f"  No updates needed for {os.path.basename(file_path)}.")

def main():
    sql_files = [f for f in os.listdir(DB_INIT_DIR) if f.endswith(".sql") and f != "mock-data-june-july.sql"]
    print(f"Found {len(sql_files)} SQL files to process.")
    for sql_file in sql_files:
        file_path = os.path.join(DB_INIT_DIR, sql_file)
        fix_sql_file(file_path)

if __name__ == "__main__":
    main()
