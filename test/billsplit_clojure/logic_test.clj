(ns billsplit-clojure.logic-test
  (:require [billsplit-clojure.logic :as l]
            [clojure.test :refer :all]
            [schema.test :as s.test]))

(use-fixtures :once s.test/validate-schemas)

;(deftest person-test
;  (testing "person model is returned correctly given data"
;    (is (= (l/person "Vitor")
;           {:name     "Vitor"
;            :products []
;            :to-pay nil}))))

;(deftest product-test
;  (testing "product model is returned correctly given data"
;    (is (= (l/product "Onion rings" 1 49.90 0)
;           {:name     "Onion rings"
;            :quantity 1
;            :price    49.90M
;            :fraction 1.0}))))
;
;(deftest add-product-test
;  (testing "product is added and fn returns person with product"
;    (let [person (l/person "John")
;          product (l/product "Fries" 1 49.90 0)]
;      (is (= (l/add-product-to-person person product)
;             {:name     "John"
;              :products [product]
;              :to-pay nil})))))