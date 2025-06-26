import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./RegisterForm.module.scss";

function RegisterForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    nickname: "",
    login: "",
    password: "",
  });
  const [loginError, setLoginError] = useState("");
  const [passwordError, setPasswordError] = useState("");

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));

    // Для логина добавим проверку
    if (name === "login") {
      if (value.length < 3) {
        setLoginError("Логин должен быть не короче 3 символов");
      } else if (value.length > 20) {
        setLoginError("Логин должен быть не длиннее 20 символов");
      } else {
        setLoginError("");
      }
    }
    if (name === "password") {
      if (value.length < 6) {
        setPasswordError("Логин должен быть не короче 6 символов");
      } else {
        setPasswordError("");
      }
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(
        "http://136.0.133.15:8080/api/users/register",
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(form),
        }
      );
      const data = await response.json();
      if (response.ok) {
        localStorage.setItem("token", data.token);
        console.log("token", data.token);
        navigate("/");
      } else {
        // Показываем ошибку из ответа сервера
        if (data.error) {
          alert(data.error); // или setError(data.error), если хочешь выводить в интерфейсе
        } else {
          alert("Ошибка регистрации");
        }
        // НЕ обязательно редиректить на /register, ты уже там.
      }
    } catch (err) {
      alert(err);
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <h2>Регистрация</h2>
      <div className={styles.nickname}>
        <label>Имя:</label>
        <input
          type="text"
          name="nickname"
          value={form.nickname}
          required
          onChange={handleChange}
        />
      </div>
      <div className={styles.login}>
        <label>Логин:</label>
        <input
          type="text"
          name="login"
          value={form.login}
          required
          minLength={3}
          maxLength={20}
          onChange={handleChange}
        />
        {loginError && (
          <div style={{ color: "red", fontSize: "0.9em" }}>{loginError}</div>
        )}
      </div>
      <div className={styles.password}>
        <label>Пароль:</label>
        <input
          type="password"
          name="password"
          value={form.password}
          required
          onChange={handleChange}
          minLength={6}
        />
        {passwordError && (
          <div style={{ color: "red", fontSize: "0.9em" }}>{passwordError}</div>
        )}
      </div>
      <button type="submit">Зарегистрироваться</button>
    </form>
  );
}

export default RegisterForm;
