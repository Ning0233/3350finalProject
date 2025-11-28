#!/usr/bin/env bash
# Generate a self-signed certificate and PKCS12 keystore at src/main/resources/keystore.p12
# Passwords and alias are intentionally simple for local dev. Change for production.
set -euo pipefail
OUT=src/main/resources
KEY=key.pem
CRT=cert.pem
P12=${OUT}/keystore.p12
PW=changeit
ALIAS=tomcat

mkdir -p "$OUT"

echo "Generating RSA key and self-signed certificate..."
openssl req -newkey rsa:2048 -nodes -keyout "$KEY" -x509 -days 365 \
  -subj "/CN=localhost" -out "$CRT"

echo "Creating PKCS12 keystore..."
openssl pkcs12 -export -in "$CRT" -inkey "$KEY" -out "$P12" -name "$ALIAS" -passout pass:"$PW"

# Clean up temporary cert/key
rm -f "$KEY" "$CRT"

echo "Keystore generated at: $P12 (password: $PW)"
echo "You can now start the app and visit https://localhost:8443/ (accept the browser warning for the self-signed cert)."
