package com.harsha.service;

import com.harsha.model.Coin;

import java.util.List;

public interface CoinService {

    List<Coin> getCoinList(int page) throws Exception;

    String getMarketChart(String coinId, int days) throws Exception;

    String getCoinDetails(String coindId) throws Exception;

    Coin findCoinById(String coinId) throws Exception;

    String searchCoin(String keyword) throws Exception;

    String getTop50CoinsByMarketCapRank() throws Exception;

    String getTradingCoins() throws Exception;

    List<Coin> getTopGainers() throws Exception;

    List<Coin> getTopLosers() throws Exception;



}
