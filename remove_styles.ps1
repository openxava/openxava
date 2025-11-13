$file = "e:\code\openxava\openxava-doc\web\docs\application_es.html"
$content = Get-Content $file -Raw
$pattern = '(?s)<style type="text/css"><!--[\s\S]*?-->\s*</style>'
$content = $content -replace $pattern, ''
[System.IO.File]::WriteAllText($file, $content, [System.Text.Encoding]::UTF8)
Write-Host "Style blocks removed from $file"
