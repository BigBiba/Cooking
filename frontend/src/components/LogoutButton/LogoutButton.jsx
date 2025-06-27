import { useNavigate } from "react-router-dom";
import styles from "./Logout.module.scss"

function LogoutButton() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");  // 1. Удаляем токен
    navigate("/login"); // 2. Перенаправляем на страницу логина (или куда надо)
  };

  return (
    <button onClick={handleLogout} className={styles.button}>Выйти</button>
  );
}

export default LogoutButton;