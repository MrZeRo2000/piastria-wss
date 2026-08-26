<#
.SYNOPSIS
    Generates the ER diagram for the test database schema.

.DESCRIPTION
    Runs generate_er_diagram.py (from common/python) against
    src/test/resources/db/schema.sql, writing db/model/erd.md (Mermaid diagram,
    for viewing) and db/model/erd.png (grayscale, white background, for printing).
    Adds Node.js to PATH first if it isn't already there, since the PNG render
    depends on mermaid-cli (mmdc/npx).

.EXAMPLE
    ./generate_erd.ps1
#>

if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    $env:Path = "$env:LOCALAPPDATA\Programs\node;$env:Path"
}

$cmd = "../common/python/venv/Scripts/python ../common/python/src/generate_er_diagram.py src/test/resources/db/schema.sql db/model/erd.md --png db/model/erd.png --exclude `"test_*`" --exclude `"sqlite_*`" --title `"Piastria schema`""
Invoke-Expression $cmd
if ($LASTEXITCODE -ne 0) {
    Write-Error "generate_er_diagram.py failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
