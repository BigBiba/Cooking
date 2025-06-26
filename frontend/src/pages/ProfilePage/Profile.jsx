import { useState, useEffect } from "react";
import LogoutButton from "components/LogoutButton/LogoutButton";
import styles from "./Profile.module.scss";
import { Link } from "react-router-dom";
import Item from "components/Item/Item";

function Profile() {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");
  const [recipes, setRecipes] = useState([]);

  //мои рецепты
  useEffect(() => {
    const token = localStorage.getItem("token"); // получаем токен

    fetch(`http://136.0.133.15:8080/api/dishes/my`, {
      headers: {
        method: "GET",
        Authorization: `Bearer ${token}`, // JWT must be sent like this
        "Content-Type": "application/json",
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Ошибка загрузки");
        return res.json();
      })
      .then((data) => {
        console.log("data from API", data);
        setRecipes(data);
      })
      .catch((err) => {
        console.error(err);
        setError(err.message);
        setRecipes(null);
      });
  }, []);

  //мой профиль
  useEffect(() => {
    const token = localStorage.getItem("token"); // получаем токен

    fetch(`http://136.0.133.15:8080/api/users/me`, {
      headers: {
        method: "POST",
        Authorization: `Bearer ${token}`, // JWT must be sent like this
        "Content-Type": "application/json",
        // если нужно, можно добавить ещё заголовки
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Ошибка загрузки");
        return res.json();
      })
      .then((data) => {
        console.log("data from API", data);
        setUser(data);
      })
      .catch((err) => {
        console.error(err);
        setError(err.message);
        setUser(null);
      });
  }, []);

  return (
    <div>
      <div className={styles.profileContainer}>
        <h1>Профиль</h1>
        <LogoutButton></LogoutButton>
      </div>
      {error && <div style={{ color: "red" }}>Ошибка: {error}</div>}
      {user ? (
        <>
          <b>Пользователь: {user.nickname}</b>
          <div className={styles.recipeContainer}>
            <h2>Мои рецепты</h2>
            <Link to="/dishes/new" className={styles.addButton}>
              <button>Добавить рецепт</button>
            </Link>
          </div>
          <div className={styles.list}>
            {recipes.length === 0 ? (
              <div>Рецептов нет.</div>
            ) : (
              recipes.map((r) => <Item key={r.id} {...r} />)
            )}
          </div>
        </>
      ) : (
        <>Загрузка...</>
      )}
    </div>
  );
}
export default Profile;
