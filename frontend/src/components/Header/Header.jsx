import { Link } from "react-router-dom";
import styles from "./Header.module.scss";
import CatalogButton from "components/CatalogButton/CatalogButton";

const Header = () => {
  const token = localStorage.getItem('token');
  console.log(token)
  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <div className={styles.catalogButton}>
          <CatalogButton></CatalogButton>
        </div>
        
        <Link to="/" className={styles.logo}>
          <h1>Cooking</h1>
        </Link>
        <div className={styles.icons}>
          <Link to="/favourites" className={styles.favIcon}>
            <img
              src="/icons/favourite.svg"
              alt="Избранное"
              width={50}
              height={50}
            />
          </Link>
          <Link to={token ? "/profile" : "/register"} className={styles.profileIcon}>
            <img
              src="/icons/account.svg"
              alt="Профиль"
              width={46}
              height={46}
            />
          </Link>
        </div>
      </div>
    </header>
  );
};

export default Header;
