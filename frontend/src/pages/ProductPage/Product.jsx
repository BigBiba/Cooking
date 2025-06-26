import { useParams } from "react-router-dom";
import styles from "./Product.module.scss";
import { useState, useEffect } from "react";

function Product() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    fetch(`/api/dishes/${id}`)
      .then(res => {
        if (!res.ok) throw new Error("Ошибка загрузки");
        return res.json();
      })
      .then(data => setProduct(data))
      .catch(err => {
        console.error(err);
        setProduct(null);
      });
  }, [id]);

  if (!product) return null;

  return (
    <div className={styles.productCard}>
      <div className={styles.header}>
        <img
          src="/dummy.png"
          alt={product.title || "Заглушка"}
          width={300}
          height={300}
        />
        <div>
          <h2 className={styles.title}>{product.title}</h2>
          <div className={styles.category}>
            <span className={styles.categoryLabel}>Категория:</span>{" "}
            {product.category}
          </div>
        </div>
      </div>

      <p className={styles.description}>{product.description}</p>

      <section className={styles.section}>
        <h3 className={styles.sectionTitle}>Ингредиенты</h3>
        <ul className={styles.ingredients}>
          {Array.isArray(product.ingredients) ? (
            product.ingredients.map((ing, i) => <li key={i}>{ing}</li>)
          ) : (
            <li>{product.ingredients}</li>
          )}
        </ul>
      </section>

      <section className={styles.section}>
        <h3 className={styles.sectionTitle}>Рецепт</h3>
        <div className={styles.recipe}>{product.recipe}</div>
      </section>
    </div>
  );
}
export default Product;
