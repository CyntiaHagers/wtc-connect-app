using Microsoft.AspNetCore.Mvc;
using WtcConnect.Api.Models;
using WtcConnect.Api.Services;

namespace WtcConnect.Api.Controllers;

[ApiController]
[Route("auth")]
public class AuthController : ControllerBase
{
    private readonly JwtService _jwtService;
    private readonly UserService _userService;

    public AuthController(JwtService jwtService, UserService userService)
    {
        _jwtService = jwtService;
        _userService = userService;
    }

    // 🔹 REGISTER (novo)
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] User user)
    {
        var existing = await _userService.GetByEmailAsync(user.Email);

        if (existing != null)
            return BadRequest(new { message = "Usuário já existe" });

        if (string.IsNullOrEmpty(user.Role))
            user.Role = "Client";

        await _userService.CreateAsync(user);

        return Ok(new { message = "Usuário criado com sucesso" });
    }

    // 🔹 LOGIN (corrigido)
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] User login)
    {
        var user = await _userService.GetByEmailAsync(login.Email);

        if (user == null || user.Password != login.Password)
        {
            return Unauthorized(new { message = "Credenciais inválidas" });
        }

        var token = _jwtService.GenerateToken(user);

        return Ok(new
        {
            token,
            id = user.Id,
            role = user.Role,
            email = user.Email
        });
    }
}