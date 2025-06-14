/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.webapplication.model;

import java.util.ArrayList;

/**
 *
 * @author ryan
 */
public interface Dao<T> {
    
   public T get(int id);   
   public ArrayList<T> getAll();
   public void insert(T t);
   public void update(T t);
   public void delete(int id);
    
}
