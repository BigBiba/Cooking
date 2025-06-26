import styles from "./CatalogButton.module.scss";
import React, { useRef, useEffect, useState } from "react";

const CatalogButton = () => {
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

  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleButtonClick = () => {
    setOpen((prev) => !prev);
  };
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div class={styles.category_dropdown} ref={dropdownRef}>
      <button class={styles.category_btn} onClick={handleButtonClick}>Каталог</button>
      <ul className={`${styles.category_list} ${open ? styles.open : ''}`}>
        {categories.map((item) => (
          <li>{item}</li>
        ))}
      </ul>
    </div>
  );
};

export default CatalogButton;
