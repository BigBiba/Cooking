import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./LoginForm.module.scss"

function LoginForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    login: "",
    password: "",
  });

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await response.json();

      if (response.ok) {
        // Сохраняем токен в localStorage
        localStorage.setItem("token", data.token);
        navigate("/");
        setForm({ email: "", password: "" }); // Сброс формы
      } else {
        navigate("/login")
      }
    } catch (err) {
      alert('Сетевая ошибка');
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <h2>Вход</h2>
      <div>
        <label>Логин:</label>
        <input
          type="text"
          name="login"
          value={form.login}
          required
          onChange={handleChange}
        />
      </div>
      <div>
        <label>Пароль:</label>
        <input
          type="password"
          name="password"
          value={form.password}
          required
          onChange={handleChange}
        />
      </div>
      <button type="submit">Войти</button>
    </form>
  );
}

export default LoginForm;
