import styles from "./Item.module.scss";
import { Link } from "react-router-dom";

function truncate(str, n) {
  return str.length > n
    ? str.slice(0, n).split(" ").slice(0, -1).join(" ") + "..."
    : str;
}

function Item(props) {
  const productUrl = `/dishes/${props.id}`;
  console.log(props.id)
  const photoUrl = props.photoUrl
  const shownDescription = truncate(props.description, 400);
  const img = document.createElement("img");
  img.src = `data:image/jpeg;base64,${photoUrl}`;
  return (
    <div className={styles.item}>
      <Link to={productUrl}>
        <img src={img.src} alt="Заглушка" width={300} height={300}></img>
      </Link>
      <div className={styles.info}>
        <Link to={productUrl} className={styles.title}>
          <h3>{props.title}</h3>
        </Link>
        <div className={styles.description}>{shownDescription}</div>
      </div>
    </div>
  );
}
export default Item;
