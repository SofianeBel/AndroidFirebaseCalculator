Write-Host "Getting SHA-1 fingerprint for Firebase..." -ForegroundColor Green

$keystorePath = "$env:USERPROFILE\.android\debug.keystore"
if (-not (Test-Path $keystorePath)) {
    Write-Host "Error: Debug keystore not found at $keystorePath" -ForegroundColor Red
    exit
}

Write-Host "Debug keystore found at: $keystorePath" -ForegroundColor Green

# Create a temporary directory
$tempDir = [System.IO.Path]::GetTempPath() + [System.Guid]::NewGuid().ToString()
New-Item -ItemType Directory -Path $tempDir | Out-Null

try {
    # Export the certificate
    $certPath = "$tempDir\debug.cer"
    Write-Host "Exporting certificate to $certPath..." -ForegroundColor Yellow
    & keytool -exportcert -alias androiddebugkey -keystore $keystorePath -file $certPath -storepass android -keypass android

    if (Test-Path $certPath) {
        Write-Host "Certificate exported successfully." -ForegroundColor Green
        
        # Get the certificate details
        $cert = New-Object System.Security.Cryptography.X509Certificates.X509Certificate2($certPath)
        
        # Get the SHA-1 thumbprint
        $sha1 = $cert.Thumbprint
        
        # Format the SHA-1 thumbprint with colons
        $formattedSha1 = for ($i = 0; $i -lt $sha1.Length; $i += 2) {
            $sha1.Substring($i, 2)
        }
        $sha1WithColons = $formattedSha1 -join ':'
        
        Write-Host "`nSHA-1 Fingerprint for Firebase:" -ForegroundColor Green
        Write-Host "SHA1: $sha1WithColons" -ForegroundColor Cyan
    } else {
        Write-Host "Failed to export certificate." -ForegroundColor Red
    }
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
} finally {
    # Clean up
    if (Test-Path $tempDir) {
        Remove-Item -Path $tempDir -Recurse -Force
    }
}

Write-Host "`nPress any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 