import Item from "components/Item/Item";
import styles from "./Home.module.scss";
import { useState, useEffect } from "react";

function Home() {
  const [recipes, setRecipes] = useState([]);

  useEffect(() => {
    fetch("/api/dishes/all")
      .then((res) => res.json())
      .then((data) => {
        setRecipes(data);
      })
      .catch((err) => {
        console.error("Ошибка при получении блюд:", err);
      });
  }, []);

  return (
    <div className={styles.recentContainer}>
      <h1>Недавно добавленные рецепты</h1>
      <div className={styles.list}>
        {recipes.map((item) => (
          <Item key={item._id || item.id || item.title} {...item} />
        ))}
      </div>
    </div>
  );
}
export default Home;
