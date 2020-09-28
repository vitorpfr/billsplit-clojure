(ns billsplit-clojure.model-test
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [billsplit-clojure.model :as m])
  (:import (clojure.lang ExceptionInfo)))

(defn match-schema [sch v]
  (try (s/validate sch v)
       (catch ExceptionInfo _)))

(defn NOT-match-schema [sch v]
  (try (not (s/validate sch v))
       (catch ExceptionInfo e
         (re-find #"Value does not match schema:" (.getMessage e)))))

(deftest Product
  (testing "throws an exception if value is nil"
    (is (NOT-match-schema m/Product nil)))

  (testing "throws an exception if value is a string"
    (is (NOT-match-schema m/Product "Fries")))

  (testing "throws an exception if value is a map with wrong type"
    (is (NOT-match-schema m/Product {:name     "Fries"
                                     :quantity 1
                                     :price    "50"
                                     :id       0})))

  (testing "throws an exception if fraction is not ratio or integer"
    (is (NOT-match-schema m/Product {:name     "Fries"
                                     :quantity 1
                                     :price    50.00M
                                     :fraction 0.2
                                     :id       0})))

  (testing "validates correct schema with and without fraction"
    (is (match-schema m/Product {:name     "Fries"
                                 :quantity 1
                                 :price    50.00M
                                 :id       0}))

    (is (match-schema m/Product {:name     "Fries"
                                 :quantity 1
                                 :price    50.00M
                                 :fraction 1/2
                                 :id       0}))))

(deftest Person
  (testing "throws an exception if value is nil"
    (is (NOT-match-schema m/Person nil)))

  (testing "throws an exception if value is a string"
    (is (NOT-match-schema m/Person "John")))

  (testing "throws an exception if value is a map with wrong type"
    (is (NOT-match-schema m/Person {:name     "John"
                                    :products {}}))

    (is (NOT-match-schema m/Person {:name     "John"
                                :products [{:name     "Fries"
                                            :quantity 1
                                            :price    50.00M
                                            :id       "0"}]})))

  (testing "validates correct schema with and without value to pay"
    (is (match-schema m/Person {:name     "John"
                                :products []}))

    (is (match-schema m/Person {:name     "John"
                                :products [{:name     "Fries"
                                            :quantity 1
                                            :price    50.00M
                                            :id       0}]}))

    (is (match-schema m/Person {:name     "John"
                                :products []
                                :to-pay   50.00M}))

    (is (match-schema m/Person {:name     "John"
                                :products []
                                :to-pay   nil}))))

(deftest Bill
  (testing "throws an exception if value is nil"
    (is (NOT-match-schema m/Bill nil)))

  (testing "throws an exception if value is a string"
    (is (NOT-match-schema m/Bill "John")))

  (testing "accepts an empty map"
    (is (match-schema m/Bill {})))

  (testing "validates correct schema"
    (is (match-schema m/Bill {"John" {:name "John"
                                      :products []}
                              "Mary" {:name "Mary"
                                      :products []}}))))


