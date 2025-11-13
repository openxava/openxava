$file = "e:\code\openxava\openxava-doc\web\docs\application_es.html"

# Read content preserving UTF-8 encoding
$reader = New-Object System.IO.StreamReader($file, [System.Text.Encoding]::UTF8)
$content = $reader.ReadToEnd()
$reader.Close()

# Remove style blocks using regex
$pattern = '(?s)<style type="text/css"><!--[\s\S]*?-->\s*</style>'
$content = $content -replace $pattern, ''

# Write back preserving UTF-8 encoding
$writer = New-Object System.IO.StreamWriter($file, $false, [System.Text.Encoding]::UTF8)
$writer.Write($content)
$writer.Close()

Write-Host "Style blocks removed from $file preserving UTF-8 encoding"
