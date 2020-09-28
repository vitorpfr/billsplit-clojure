(ns billsplit-clojure.adapters-test
  (:require [clojure.test :refer :all]
            [billsplit-clojure.adapters :as a]
            [schema.test :as s.test]))

(use-fixtures :once s.test/validate-schemas)

(deftest tip-wire->internal
  (testing "converts tip correctly from params"
    (is (= (a/tip-wire->internal {:tipswitch "on"
                                  :tipvalue  "20"})
           0.2))
    (is (= (a/tip-wire->internal {:tipvalue "10"})
           nil))))

(deftest products-wire->internal
  (testing "converts products correctly from params"
    (is (= (a/products-wire->internal {:products   ["Onion" "Fries"]
                                       :quantities ["1" "2"]
                                       :values     ["50" "30"]})
           [{:id       0
             :name     "Onion"
             :price    50M
             :quantity 1}
            {:id       1
             :name     "Fries"
             :price    30M
             :quantity 2}]))))

(deftest consumed-wire->internal
  (testing "converts consumption data correctly from params"
    (is (= (a/consumed-wire->internal ["John" "Mary"]
                                      {:products ["Onion rings" "Fries"]
                                       :consumed0_0 "on"
                                       :consumed1_0 "on"
                                       :consumed0_1 "on"})
           {0 ["John" "Mary"]
            1 ["John"]}))

    (is (= (a/consumed-wire->internal ["John" "Mary" "Paul" "Laura"]
                                      {:products ["Onion rings" "Fries" "Soda" "Ice cream"]
                                       :consumed0_0 "on"
                                       :consumed0_1 "on"
                                       :consumed0_2 "on"
                                       :consumed0_3 "on"
                                       :consumed1_0 "on"
                                       :consumed1_2 "on"
                                       :consumed2_0 "on"
                                       :consumed2_1 "on"
                                       :consumed3_3 "on"})
           {0 ["John" "Mary" "Paul"]
            1 ["John" "Paul"]
            2 ["John" "Mary"]
            3 ["John" "Laura"]}))))

(deftest value-internal->wire
  (testing "converts value correctly to be displayed externally"
    (is (= (a/value-internal->wire 50.5M)
           "R$ 50.50"))

    (is (= (a/value-internal->wire 40.495M)
           "R$ 40.50"))

    (is (= (a/value-internal->wire 30.0000000001M)
           "R$ 30.00"))

    (is (= (a/value-internal->wire 0.49M)
           "R$ 0.49"))))