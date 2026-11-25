const passwordField = document.querySelector("#password");
const passwordToggle = document.querySelector("[data-password-toggle]");

if (passwordField && passwordToggle) {
	passwordToggle.addEventListener("click", () => {
		const showPassword = passwordField.type === "password";
		passwordField.type = showPassword ? "text" : "password";
		passwordToggle.setAttribute("aria-pressed", String(showPassword));
		passwordToggle.setAttribute("aria-label", showPassword ? "Ocultar senha" : "Mostrar senha");
	});
}
