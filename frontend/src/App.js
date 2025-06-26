import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css';
import Layout from './layouts/Layout';
import Home from './pages/HomePage/Home';
import Profile from './pages/ProfilePage/Profile';
import Favourite from './pages/Favourite';
import Product from './pages/ProductPage/Product';
import Login from 'pages/LoginPage/Login';
import Register from 'pages/RegisterPage/Register';
import Catalog from 'pages/Catalog';
import NewRecipe from 'pages/NewRecipePage/NewRecipe';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path='/' element={<Layout/>}>
          <Route index element={<Home/>}/>
          <Route path='/profile' element={<Profile/>}/>
          <Route path="/favourites" element={<Favourite/>}/>
          <Route path="/dishes/:id" element={<Product/>}/>
          <Route path="/login" element={<Login/>}/>
          <Route path="/register" element={<Register/>}/>
          <Route path="/catalog" element={<Catalog/>}/>
          <Route path="/dishes/new" element={<NewRecipe/>}/>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
