(ns billsplit-clojure.logic
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))


(s/defn ^:private empty-bill :- m/Bill [] {})

(s/defn ^:private person :- m/Person
  [name :- s/Str]
  {:name     name
   :products []})

(s/defn ^:private add-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str]
  (assoc bill person-name (person person-name)))

(s/defn add-people-list-to-empty-bill :- m/Bill
  [people-list :- [s/Str]]
  (reduce add-person (empty-bill) people-list))

(s/defn ^:private add-product-to-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str
   product :- m/Product]
  (update-in bill [person-name :products] conj product))

(s/defn ^:private add-product-to-bill :- m/Bill
  [bill :- m/Bill
   person-list :- [s/Str]
   product :- m/Product]
  (let [fractions (repeat (count person-list) (/ 1 (count person-list)))
        product-fractions (zipmap person-list fractions)]
    (reduce-kv (fn [bill person-name fraction]
                 (add-product-to-person bill
                                        person-name
                                        (assoc product :fraction fraction)))
               bill
               product-fractions)))

(s/defn add-product-list-to-bill
  [bill :- m/Bill
   products :- [m/Product]
   who-consumed]
  (reduce (fn [bill {:keys [id] :as product}]
            (add-product-to-bill bill (get who-consumed id) product))
          bill
          products))

(s/defn ^:private calculate-product-cost
  [{:keys [quantity price fraction]} :- m/Product]
  (* quantity price fraction))

(s/defn ^:private calculate-person-cost :- m/Person
  [person :- m/Person]
  (let [cost-per-product (map calculate-product-cost (:products person))]
    (assoc person :to-pay (bigdec (reduce + cost-per-product)))))

(defn ^:private update-map [m f]
  (reduce-kv (fn [m k v]
               (assoc m k (f v))) {} m))

(s/defn calculate-people-costs :- m/Bill
  [bill :- m/Bill]
  (update-map bill calculate-person-cost))

(s/defn add-tip :- m/Bill
  [bill :- m/Bill
   tip-value]
  (if tip-value
    (update-map bill (fn [person] (update person
                                          :to-pay
                                          #(* (bigdec (inc tip-value)) %))))
    bill))

(defn get-total-bill-value
  [bill]
  (reduce + (map #(:to-pay %) (vals bill))))
