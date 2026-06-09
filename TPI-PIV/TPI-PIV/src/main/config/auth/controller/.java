@PostMapping("/refresh-token")
public ResponseEntity<AuthResponseDTO> refreshToken(
        @Valid @RequestBody RefreshTokenRequestDTO dto
) {
    return ResponseEntity.ok(authService.refreshToken(dto));
}