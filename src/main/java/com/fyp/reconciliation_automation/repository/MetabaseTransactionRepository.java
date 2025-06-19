//package com.payaza.reconciliation_automation.repository;
//
//import com.payaza.reconciliation_automation.entity.MetabaseTransaction;
//import entity.com.fyp.reconciliation_automation.MetabaseTransactionDTO;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//public interface MetabaseTransactionRepository extends JpaRepository<MetabaseTransaction, Long> {
//
//    @Query(value = "SELECT * FROM public.payaza_account_transaction WHERE DATE(transaction_date) = :date", nativeQuery = true)
//    List<MetabaseTransaction> findByTransactionDate(@Param("date") LocalDate date);
//
//
//    @Query(value = "SELECT * FROM public.payaza_account_transaction WHERE transaction_date >= :startDate AND transaction_date < :endDate", nativeQuery = true)
//    List<MetabaseTransaction> findByTransactionDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
//}