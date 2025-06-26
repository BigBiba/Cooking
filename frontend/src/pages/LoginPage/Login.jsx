import LoginForm from "components/LoginForm/LoginForm";
import { Link } from "react-router-dom";
import styles from "./Login.module.scss";

function Login() {
  return (
    <div className={styles.page}>
      <LoginForm />
      <div className={styles.redirect}>
        <div>Еще нет аккаунта?</div>
        <Link to="/register">Создать аккаунт</Link>
      </div>
    </div>
  );
}
export default Login;
