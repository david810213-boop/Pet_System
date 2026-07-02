# 測試腳本（PowerShell 版）：驗證 JWT 攔截器改動
# 用法：cd 到專案根目錄後執行 .\test-auth.ps1
# 如果跳出「無法執行指令碼」的錯誤，先執行：
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

$BASE = "http://localhost:8081"

Write-Host "`n== 1. 未登入打 /api/users/me，應該回 401 JSON ==" -ForegroundColor Cyan
try {
    Invoke-WebRequest -Uri "$BASE/api/users/me" -Method GET -ErrorAction Stop
} catch {
    Write-Host "狀態碼: $($_.Exception.Response.StatusCode.value__)"
    Write-Host $_.ErrorDetails.Message
}

Write-Host "`n== 2. 帳密登入（admin），用 Session(Cookie) 模式 ==" -ForegroundColor Cyan
$session = $null
$loginResp = Invoke-WebRequest -Uri "$BASE/auth/login/submit" -Method POST `
    -Body @{ username = "admin@pet.com"; password = "admin123" } `
    -SessionVariable session -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "登入回應狀態碼: $($loginResp.StatusCode)"

Write-Host "`n== 3. 帶著 Cookie 打 /api/users/me，應該成功（ADMIN）==" -ForegroundColor Cyan
$meResp = Invoke-WebRequest -Uri "$BASE/api/users/me" -WebSession $session
Write-Host $meResp.Content

Write-Host "`n== 4. 用顧客帳號登入，改用 Authorization: Bearer 測試 ==" -ForegroundColor Cyan
$custSession = $null
$loginResp2 = Invoke-WebRequest -Uri "$BASE/auth/login/submit" -Method POST `
    -Body @{ username = "user@pet.com"; password = "user123" } `
    -SessionVariable custSession -MaximumRedirection 0 -ErrorAction SilentlyContinue

$cookie = $custSession.Cookies.GetCookies("$BASE") | Where-Object { $_.Name -eq "JWT_TOKEN" }
$token = $cookie.Value
Write-Host "取得的 token: $token"

Write-Host "`n== 5. 用 Bearer token 打 /api/pets/my（顧客查自己的寵物，應該成功）==" -ForegroundColor Cyan
$headers = @{ Authorization = "Bearer $token" }
$petsResp = Invoke-WebRequest -Uri "$BASE/api/pets/my" -Headers $headers
Write-Host $petsResp.Content

Write-Host "`n== 6. 用顧客身分（非 ADMIN）打 /api/users，應該回 403 JSON ==" -ForegroundColor Cyan
try {
    Invoke-WebRequest -Uri "$BASE/api/users" -Headers $headers -ErrorAction Stop
} catch {
    Write-Host "狀態碼: $($_.Exception.Response.StatusCode.value__)"
    Write-Host $_.ErrorDetails.Message
}

Write-Host "`n== 7. 用顧客身分打 /api/payments/report，應該回 403 JSON ==" -ForegroundColor Cyan
try {
    Invoke-WebRequest -Uri "$BASE/api/payments/report" -Headers $headers -ErrorAction Stop
} catch {
    Write-Host "狀態碼: $($_.Exception.Response.StatusCode.value__)"
    Write-Host $_.ErrorDetails.Message
}

Write-Host "`n測試完成，請檢查上面各步驟的狀態碼與回應內容是否符合預期。" -ForegroundColor Green
