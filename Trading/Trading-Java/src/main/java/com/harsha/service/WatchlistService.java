package com.harsha.service;

import com.harsha.model.Coin;
import com.harsha.model.User;
import com.harsha.model.Wallet;
import com.harsha.model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;

    Coin addToWatchlist(Coin coin, User user) throws Exception;
}
