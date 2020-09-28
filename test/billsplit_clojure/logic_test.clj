(ns billsplit-clojure.logic-test
  (:require [billsplit-clojure.logic :as l]
            [clojure.test :refer :all]
            [schema.test :as s.test]))

(use-fixtures :once s.test/validate-schemas)

(deftest add-people-list-to-empty-bill
  (testing "function returns bill with people list added"
    (is (= (l/add-people-list-to-empty-bill ["John" "Mary"])
           {"John" {:name     "John"
                    :products []}
            "Mary" {:name     "Mary"
                    :products []}}))

    (is (= (l/add-people-list-to-empty-bill [])
           {}))))

(deftest add-product-list-to-bill
  (testing "returns bill with products added"
    (is (= (l/add-product-list-to-bill {"John" {:name     "John"
                                                :products []}
                                        "Mary" {:name     "Mary"
                                                :products []}}
                                       [{:id       0
                                         :name     "Onion"
                                         :price    50M
                                         :quantity 1}
                                        {:id       1
                                         :name     "Fries"
                                         :price    30M
                                         :quantity 2}]
                                       {0 ["John" "Mary"]
                                        1 ["John"]})
           {"John" {:name     "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}
                               {:fraction 1
                                :id       1
                                :name     "Fries"
                                :price    30M
                                :quantity 2}]}
            "Mary" {:name     "Mary"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]}}))))


(deftest calculate-people-costs
  (testing "amount each person has to pay is added to bill"
    (is (= (l/calculate-people-costs {"John" {:name     "John"
                                              :products [{:fraction 1/2
                                                          :id       0
                                                          :name     "Onion"
                                                          :price    50M
                                                          :quantity 1}
                                                         {:fraction 1
                                                          :id       1
                                                          :name     "Fries"
                                                          :price    30M
                                                          :quantity 2}]}
                                      "Mary" {:name     "Mary"
                                              :products [{:fraction 1/2
                                                          :id       0
                                                          :name     "Onion"
                                                          :price    50M
                                                          :quantity 1}]}})
           {"John" {:name     "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}
                               {:fraction 1
                                :id       1
                                :name     "Fries"
                                :price    30M
                                :quantity 2}]
                    :to-pay     85.0M}
            "Mary" {:name     "Mary"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay     25.0M}}))))

(l/add-tip {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 50.0M}
            "Mary" {:name "Mary"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 20.0M}}
           0.2)

(deftest add-tip
  (testing "tip is added to the to-pay value, if there is one"
    (is (= (l/add-tip {"John" {:name "John"
                               :products [{:fraction 1/2
                                           :id       0
                                           :name     "Onion"
                                           :price    50M
                                           :quantity 1}]
                               :to-pay 50.0M}
                       "Mary" {:name "Mary"
                               :products [{:fraction 1/2
                                          :id       0
                                          :name     "Onion"
                                          :price    50M
                                          :quantity 1}]
                               :to-pay 20.0M}}
                      0.2)
           {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 60.00M}
            "Mary" {:name "Mary"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 24.00M}}))

    (is (= (l/add-tip {"John" {:name "John"
                               :products [{:fraction 1/2
                                           :id       0
                                           :name     "Onion"
                                           :price    50M
                                           :quantity 1}]
                               :to-pay 50.0M}
                       "Mary" {:name "Mary"
                               :products [{:fraction 1/2
                                           :id       0
                                           :name     "Onion"
                                           :price    50M
                                           :quantity 1}]
                               :to-pay 20.0M}}
                      nil)
           {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 50.00M}
            "Mary" {:name "Mary"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Onion"
                                :price    50M
                                :quantity 1}]
                    :to-pay 20.00M}}))))

(deftest get-total-bill-value
  (testing "total-bill-value is correctly calculated"
    (is (= (l/get-total-bill-value {"John" {:name "John"
                                            :products [{:fraction 1/2
                                                        :id       0
                                                        :name     "Onion"
                                                        :price    50M
                                                        :quantity 1}]
                                            :to-pay 60.00M}
                                    "Mary" {:name "Mary"
                                            :products [{:fraction 1/2
                                                        :id       0
                                                        :name     "Onion"
                                                        :price    50M
                                                        :quantity 1}]
                                            :to-pay 24.30M}})
           84.30M))

    (is (= (l/get-total-bill-value {"John" {:name "John"
                                            :products [{:fraction 1
                                                        :id       0
                                                        :name     "Onion"
                                                        :price    50M
                                                        :quantity 1}]
                                            :to-pay 30.00M}})
           30.00M))))