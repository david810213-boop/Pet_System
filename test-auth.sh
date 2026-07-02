#!/usr/bin/env bash
# 測試腳本：驗證 JWT 攔截器改動（Bearer header 支援 + API 路徑回 JSON 401/403）
# 用既有店家帳密登入流程測試，不需要真的 LINE Channel 也能跑
#
# 使用方式：
#   1. 先啟動專案：mvn spring-boot:run
#   2. chmod +x test-auth.sh && ./test-auth.sh

BASE="http://localhost:8081"

echo "== 1. 未登入打 /api/users/me，應該回 401 JSON =="
curl -s -i "$BASE/api/users/me" | head -n 20
echo -e "\n"

echo "== 2. 帳密登入（admin），用 Cookie 模式，確認店家網頁版沒壞 =="
curl -s -i -c cookie.txt -X POST "$BASE/auth/login/submit" \
  -d "username=admin@pet.com" -d "password=admin123" | head -n 10
echo -e "\n"

echo "== 3. 帶著 Cookie 打 /api/users/me，應該成功（ADMIN）=="
curl -s -b cookie.txt "$BASE/api/users/me"
echo -e "\n\n"

echo "== 4. 用一般顧客帳號登入，改用 Authorization: Bearer 測試（驗證新加的 header 支援）=="
LOGIN_RESPONSE=$(curl -s -i -X POST "$BASE/auth/login/submit" \
  -d "username=user@pet.com" -d "password=user123")
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "Set-Cookie: JWT_TOKEN=" | sed -E 's/.*JWT_TOKEN=([^;]+);.*/\1/')
echo "取得的 token: $TOKEN"
echo -e "\n"

echo "== 5. 用 Bearer token 打 /api/pets/my（顧客自己查寵物，應該成功）=="
curl -s -i "$BASE/api/pets/my" -H "Authorization: Bearer $TOKEN"
echo -e "\n\n"

echo "== 6. 用顧客身分（非 ADMIN）打 /api/users（ADMIN 限定），應該回 403 JSON =="
curl -s -i "$BASE/api/users" -H "Authorization: Bearer $TOKEN"
echo -e "\n\n"

echo "== 7. 用顧客身分打 /api/payments/report（ADMIN 限定），應該回 403 JSON =="
curl -s -i "$BASE/api/payments/report" -H "Authorization: Bearer $TOKEN"
echo -e "\n\n"

rm -f cookie.txt
echo "測試完成，請檢查上面各步驟的 HTTP 狀態碼與回應內容是否符合預期。"
