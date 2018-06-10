package com.zeratul.plugin.java;

/**
 * @author dreamyao
 * @title
 * @date 2018/4/12 下午10:28
 * @since 1.0.0
 */
public class Pair<K,V> {

    private K key;
    private V value;

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }

    public int hashCode() {
        return this.key.hashCode() * 13 + (this.value == null?0:this.value.hashCode());
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(!(obj instanceof Pair)) {
            return false;
        } else {
            Pair var2 = (Pair)obj;
            if(this.key != null) {
                if(!this.key.equals(var2.key)) {
                    return false;
                }
            } else if(var2.key != null) {
                return false;
            }

            if(this.value != null) {
                if(!this.value.equals(var2.value)) {
                    return false;
                }
            } else if(var2.value != null) {
                return false;
            }

            return true;
        }
    }
}
