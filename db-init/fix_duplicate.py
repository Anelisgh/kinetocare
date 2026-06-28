import os

file_path = r"c:\Users\Aneliss\Desktop\kinetocare\db-init\programari_service.sql"

def main():
    if not os.path.exists(file_path):
        print("Error: programari_service.sql not found.")
        return
        
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    # We want to target the record for ID 50.
    # It looks like: ,(50,_binary '\0',_binary '\0','2026-02-16 10:00:00.000000','2026-03-30',50,7,'ANULAT_DE_PACIENT','10:00:00.000000','10:50:00.000000'
    # Let's find this specific substring.
    target = ",(50,_binary '\\0',_binary '\\0','2026-02-16 10:00:00.000000','2026-03-30',50,7,'ANULAT_DE_PACIENT','10:00:00.000000','10:50:00.000000'"
    replacement = ",(50,_binary '\\0',_binary '\\0','2026-02-16 10:00:00.000000','2026-03-30',50,7,'ANULAT_DE_PACIENT','10:05:00.000000','10:55:00.000000'"
    
    if target in content:
        content = content.replace(target, replacement)
        print("Replacement successful using exact string!")
    else:
        # Let's try a fallback: search for ,(50, and 2026-03-30 and the times, and replace just the times
        # We find the start index of the record
        search_str = ",(50,"
        idx = content.find(search_str)
        found = False
        while idx != -1:
            # Check if this is our record (contains '2026-03-30' and 'ANULAT_DE_PACIENT' within next 500 chars)
            window = content[idx:idx+500]
            if "2026-03-30" in window and "ANULAT_DE_PACIENT" in window and "'10:00:00.000000','10:50:00.000000'" in window:
                new_window = window.replace("'10:00:00.000000','10:50:00.000000'", "'10:05:00.000000','10:55:00.000000'")
                content = content[:idx] + new_window + content[idx+500:]
                found = True
                print("Replacement successful using fallback window search!")
                break
            idx = content.find(search_str, idx + 1)
            
        if not found:
            print("Error: Could not locate target record for ID 50!")
            return

    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

if __name__ == "__main__":
    main()
