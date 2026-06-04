import os
import re

directory = r"c:\Users\Aneliss\Desktop\kinetocare\Thesis\Lucrare Scrisa"

# We will define a function to fix Mojibake (double-encoded UTF-8)
def fix_mojibake(text):
    # Try to encode as cp1252 / latin1 and decode as utf-8 to fix Mojibake
    try:
        # Standard Mojibake conversion
        # e.g., 'fÄƒrÄƒ' -> encode('cp1252' or 'latin1') -> decode('utf-8') -> 'fără'
        # Let's try latin-1 first as it maps 1-to-1 for bytes 0-255.
        byte_data = text.encode('latin-1')
        fixed = byte_data.decode('utf-8')
        return fixed
    except Exception as e:
        # If it fails, let's try cp1252
        try:
            byte_data = text.encode('cp1252')
            fixed = byte_data.decode('utf-8')
            return fixed
        except Exception:
            return text

# A more robust regex replacement approach in case of mixed encoding
replacements = {
    "Äƒ": "ă",
    "Ã®": "î",
    "È™": "ș",
    "È›": "ț",
    "Ã¢": "â",
    "ÄŒ": "Ă",
    "ÃŽ": "Î",
    "È˜": "Ș",
    "Èš": "Ț",
    "Ã‚": "Â",
    "â€”": "—",
    "â€“": "–",
    "â„¢": "™",
    "â€¢": "•",
    "â€¦": "…",
    "Ä": "ă", # Fallback for truncated/broken sequences
}

def regex_fix(text):
    # Let's replace common patterns first
    t = text
    # Try the encoding decode first
    fixed = fix_mojibake(t)
    # Check if standard diacritics are recovered
    if "ă" in fixed or "ș" in fixed or "ț" in fixed or "î" in fixed or "â" in fixed:
        t = fixed
    
    # Fallback to manual replacements for any residual Mojibake
    for bad, good in replacements.items():
        t = t.replace(bad, good)
        
    return t

def remove_frontmatter(text):
    # Match YAML frontmatter at the very start of the file
    # Pattern: ^---\s*\n(.*?)\n---\s*\n
    pattern = r"^---\s*\n.*?\n---\s*\n"
    # Use re.DOTALL to make . match newlines, and re.MULTILINE if needed, but ^ matches start of string
    t = re.sub(pattern, "", text, flags=re.DOTALL)
    # Also strip any leading whitespace/newlines
    return t.lstrip()

# Iterate through all files in the folder
for filename in os.listdir(directory):
    if filename.endswith(".md"):
        filepath = os.path.join(directory, filename)
        print(f"Processing: {filename}")
        
        # Read file. Since it might have been written in cp1252 or utf-8,
        # let's try to read it as UTF-8 first.
        try:
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
        except Exception:
            with open(filepath, "r", encoding="cp1252") as f:
                content = f.read()
        
        # Remove YAML frontmatter
        cleaned_content = remove_frontmatter(content)
        
        # Repair diacritics
        fixed_content = regex_fix(cleaned_content)
        
        # Write back as pure UTF-8
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(fixed_content)
        
        print(f"Successfully updated: {filename}")

print("All files processed!")
