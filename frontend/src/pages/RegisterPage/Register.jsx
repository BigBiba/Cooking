import RegisterForm from "components/RegisterForm/RegisterForm";
import { Link } from "react-router-dom";
import styles from "./Register.module.scss";

function Register() {
  return (
    <div className={styles.page}>
      <RegisterForm></RegisterForm>
      <div className={styles.redirect}>
        <div>Уже есть аккаунт?</div>
        <Link to="/login">Войти</Link>
      </div>
    </div>
  );
}
export default Register;
