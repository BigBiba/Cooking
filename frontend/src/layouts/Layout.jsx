import React from 'react';
import Header from '../components/Header/Header';
import styles from './Layout.module.scss'
import { Outlet } from 'react-router-dom';

const Layout = ({ children }) => {
    
  return (
    <div className={styles.layout}>
      <Header></Header>
      <main>
        <Outlet/>
      </main>
    </div>
  );
};

export default Layout;