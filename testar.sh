#!/usr/bin/env bash
set -euo pipefail

# Ecommerce Pedidos Service - Script de teste rápido
API="${API:-http://localhost:8080}"

print_json() {
  if command -v jq >/dev/null 2>&1; then
    jq .
  else
    cat
  fi
}

echo "Limpando memória da aplicação..."
curl -s -X DELETE "$API/pedidos/memoria" >/dev/null || true

echo "Publicando pedido válido..."
curl -s -X POST "$API/pedidos" \
  -H "Content-Type: application/json" \
  -d '{"pedidoId":123,"clienteId":10,"valor":250.00}' | print_json

echo
echo "Aguardando consumer processar..."
sleep 2

echo "Mensagens recebidas:"
curl -s "$API/pedidos/recebidos" | print_json

echo
echo "Publicando pedido que simula falha no consumer e cai na DLT..."
curl -s -X POST "$API/pedidos" \
  -H "Content-Type: application/json" \
  -d '{"pedidoId":456,"clienteId":999,"valor":150.00}' | print_json

echo
echo "Aguardando retry/DLT..."
sleep 10

echo "Mensagens na DLT:"
curl -s "$API/pedidos/dlt" | print_json
