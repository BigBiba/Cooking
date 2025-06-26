import styles from "./NewRecipe.module.scss";
import { useState, useRef } from "react";

function NewRecipe() {
  const categories = [
    "Завтраки",
    "Супы",
    "Основные блюда",
    "Гарниры",
    "Закуски",
    "Дессерты",
    "Напитки",
    "Соусы",
    "Салаты",
    "Выпечка",
  ];
  const fileInputRef = useRef(null);
  const [recipe, setRecipe] = useState({
    title: "",
    description: "",
    category: "",
    ingredients: [],
    recipe: "",
    photoURL: "",
  });
  const [photo, setPhoto] = useState(null);
  const [ingredientInput, setIngredientInput] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRecipe((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePhotoChange = (e) => {
    setPhoto(e.target.files[0]);
  };

  const removePhoto = () => {
    setPhoto(null);
    // Сброс файла в input (очищаем value)
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const handleAddIngredient = (e) => {
    e.preventDefault();
    const trimmed = ingredientInput.trim();
    if (trimmed && !recipe.ingredients.includes(trimmed)) {
      setRecipe((prev) => ({
        ...prev,
        ingredients: [...prev.ingredients, trimmed],
      }));
      setIngredientInput("");
    }
  };

  const handleRemoveIngredient = (ingredient) => {
    setRecipe((prev) => ({
      ...prev,
      ingredients: prev.ingredients.filter((i) => i !== ingredient),
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("title", recipe.title);
    formData.append("description", recipe.description);
    formData.append("category", recipe.category);
    formData.append("ingredients", JSON.stringify(recipe.ingredients));
    formData.append("recipe", recipe.recipe);

    if (photo) {
      formData.append("photo", photo);
    }

    const token = localStorage.getItem("token");

    fetch("/api/dishes", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    })
      .then((res) => res.json())
      .then((data) => {
        console.log("Recipe saved:", data);
      })
      .catch((err) => {
        console.error("Error:", err);
      });
  };

  return (
    <div className={styles.newRecipe}>
      <h2>Создать новый рецепт</h2>
      <form className={styles.form} onSubmit={handleSubmit}>
        <label>
          Название:
          <input
            type="text"
            name="title"
            value={recipe.title}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Описание:
          <textarea
            name="description"
            value={recipe.description}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Категория:
          <select
            name="category"
            value={recipe.category}
            onChange={handleChange}
            required
            className={styles.categorySelect}
          >
            <option value="" disabled>
              Выберите категорию
            </option>
            {categories.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </label>
        <label>
          Ингредиенты:
          <div className={styles.ingredientsInputWrapper}>
            <input
              type="text"
              value={ingredientInput}
              onChange={(e) => setIngredientInput(e.target.value)}
              placeholder="Введите ингредиент"
            />
            <button
              type="button"
              onClick={handleAddIngredient}
              disabled={!ingredientInput.trim()}
            >
              Добавить
            </button>
          </div>
          <ul className={styles.ingredientsList}>
            {recipe.ingredients.map((item, idx) => (
              <li key={item + idx}>
                {item}
                <button
                  type="button"
                  onClick={() => handleRemoveIngredient(item)}
                  className={styles.ingredientRemoveBtn}
                  title="Удалить ингредиент"
                >
                  ×
                </button>
              </li>
            ))}
          </ul>
        </label>
        <label>
          Рецепт приготовления:
          <textarea
            name="recipe"
            value={recipe.recipe}
            onChange={handleChange}
            required
          />
        </label>
        <label>
          Фото:
          <div className={styles.photoInputWrapper}>
            <input
              type="file"
              name="photo"
              accept="image/*"
              onChange={handlePhotoChange}
              className={styles.photoInput}
              ref={fileInputRef}
            />
            {photo && (
              <button
                type="button"
                className={styles.photoRemoveBtn}
                title="Удалить фото"
                onClick={removePhoto}
              >
                ×
              </button>
            )}
          </div>
        </label>
        <button type="submit">Сохранить рецепт</button>
      </form>
    </div>
  );
}

export default NewRecipe;
