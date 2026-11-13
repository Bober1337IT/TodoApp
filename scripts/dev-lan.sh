#!/usr/bin/env bash

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_PROPS="$ROOT/android/local.properties"
DEFAULT_URL="http://10.0.2.2:8080/"

detect_lan_ip() {
  if command -v ip >/dev/null 2>&1; then
    local from_route
    from_route="$(ip -4 route get 1.1.1.1 2>/dev/null | awk '{for (i = 1; i <= NF; i++) if ($i == "src") { print $(i + 1); exit }}')"
    if [[ -n "$from_route" && "$from_route" != 127.* ]]; then
      printf '%s\n' "$from_route"
      return
    fi
    ip -4 -o addr show scope global 2>/dev/null \
      | awk '!/ (docker0|br-|veth|virbr)/ { split($4, a, "/"); print a[1]; exit }'
    return
  fi
  hostname -I 2>/dev/null | awk '{print $1}'
}

usage() {
  cat <<'EOF'
Usage: scripts/dev-lan.sh [--up] [--emulator]

  --up         run: docker compose up -d
  --emulator   write emulator URL (10.0.2.2) instead of LAN IP

Writes api.base.url into android/local.properties.
Gradle reads it into BuildConfig.BASE_URL.
Android Studio usually creates sdk.dir there for you.
EOF
}

DO_UP=0
USE_EMULATOR=0
for arg in "$@"; do
  case "$arg" in
    --up) DO_UP=1 ;;
    --emulator) USE_EMULATOR=1 ;;
    -h|--help) usage; exit 0 ;;
    *) echo "Unknown arg: $arg" >&2; usage; exit 1 ;;
  esac
done

if [[ "$DO_UP" -eq 1 ]]; then
  (cd "$ROOT" && docker compose up -d)
fi

if [[ "$USE_EMULATOR" -eq 1 ]]; then
  BASE_URL="$DEFAULT_URL"
  LAN_IP="10.0.2.2"
else
  LAN_IP="$(detect_lan_ip || true)"
  if [[ -z "${LAN_IP:-}" ]]; then
    echo "Could not detect LAN IP. Falling back to emulator URL." >&2
    BASE_URL="$DEFAULT_URL"
    LAN_IP="10.0.2.2"
  else
    BASE_URL="http://${LAN_IP}:8080/"
  fi
fi

touch "$LOCAL_PROPS"
if [[ -s "$LOCAL_PROPS" ]] && [[ -n "$(tail -c 1 "$LOCAL_PROPS" || true)" ]]; then
  printf '\n' >> "$LOCAL_PROPS"
fi
grep -v '^api\.base\.url=' "$LOCAL_PROPS" > "${LOCAL_PROPS}.tmp" || true
sed -i -E 's/(sdk\.dir=.*\/Sdk)api\.base\.url=.*/\1/' "${LOCAL_PROPS}.tmp"
mv "${LOCAL_PROPS}.tmp" "$LOCAL_PROPS"
printf 'api.base.url=%s\n' "$BASE_URL" >> "$LOCAL_PROPS"

echo "LAN / target host: $LAN_IP"
echo "Wrote api.base.url=$BASE_URL"
echo "File: $LOCAL_PROPS"
echo
echo "Rebuild the Android app so BuildConfig.BASE_URL updates."
echo "Phone and PC must be on the same Wi-Fi; allow TCP 8080 in the firewall if needed:"
echo "  sudo firewall-cmd --add-port=8080/tcp"
echo "  # or permanently: sudo firewall-cmd --add-port=8080/tcp --permanent && sudo firewall-cmd --reload"
