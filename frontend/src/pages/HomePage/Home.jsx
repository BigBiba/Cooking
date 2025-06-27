import Item from "components/Item/Item";
import styles from "./Home.module.scss";
import { useState, useEffect } from "react";

function Home() {
  const [recipes, setRecipes] = useState([]);
  console.log("data home", recipes)
  useEffect(() => {
    fetch("http://136.0.133.15:8080/api/dishes/all")
      .then((res) => res.json())
      .then((data) => {
        console.log("data home", data);
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
