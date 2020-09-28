(ns billsplit-clojure.controllers-test
  (:require [billsplit-clojure.controllers :as c]
            [clojure.test :refer :all]
            [schema.test :as s.test]))

(use-fixtures :once s.test/validate-schemas)

(deftest create-bill
  (testing "bill is created using parameters"
    (is (= (c/create-bill ["John" "Mary"]
                          {:products ["Fries"], :quantities ["1"], :values ["30"], :consumed0_0 "on", :consumed1_0 "on", :tipswitch "on", :tipvalue "10"})
           {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1}]}
            "Mary" {:name "Mary"
                    :products [{:id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1
                                :fraction 1/2}]}}))

    (is (= (c/create-bill ["John" "Mary"]
                          {:products ["Fries" "Soda"], :quantities ["1" "2"], :values ["30" "10"], :consumed0_0 "on", :consumed1_0 "on", :consumed0_1 "on", :tipswitch "on", :tipvalue "10"})
           {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1}
                               {:fraction 1
                                :id       1
                                :name     "Soda"
                                :price    10M
                                :quantity 2}]}
            "Mary" {:name "Mary"
                    :products [{:id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1
                                :fraction 1/2}]}}))))

(deftest calculate-bill
  (testing "given a bill with products, calculate how much each person has to pay"
    (is (= (c/calculate-bill {"John" {:name "John"
                                      :products [{:fraction 1/2
                                                  :id       0
                                                  :name     "Fries"
                                                  :price    30M
                                                  :quantity 1}]}
                              "Mary" {:name "Mary"
                                      :products [{:id       0
                                                  :name     "Fries"
                                                  :price    30M
                                                  :quantity 1
                                                  :fraction 1/2}]}}
                             {:tipswitch "on", :tipvalue "10"})
           {"John" {:name "John"
                    :products [{:fraction 1/2
                                :id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1}]
                    :to-pay 16.5M}
            "Mary" {:name "Mary"
                    :products [{:id       0
                                :name     "Fries"
                                :price    30M
                                :quantity 1
                                :fraction 1/2}]
                    :to-pay 16.5M}}))))