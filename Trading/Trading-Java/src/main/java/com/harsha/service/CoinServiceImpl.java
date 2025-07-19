package com.harsha.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsha.model.Coin;
import com.harsha.repository.CoinRepository;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class CoinServiceImpl implements CoinService{

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Cacheable(value = "coinList", key = "#page", unless = "#result == null")
    public List<Coin> getCoinList(int page) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=USD&per_page=10&page=" + page;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
            List<Coin> coinList = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<Coin>>(){});
            return coinList;
        }
        catch (HttpClientErrorException.TooManyRequests e) {
            System.out.println("Rate limit hit: " + e.getResponseBodyAsString());
            throw new Exception("Too Many Requests: Please try again later.");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("API error: " + e.getResponseBodyAsString());
            throw new Exception("API call failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
            throw new Exception("Server Error: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "coinCharts", key = "#coinId + '_' + #days", unless = "#result == null")
    public String getMarketChart(String coinId, int days) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/"+coinId+"/market_chart?vs_currency=USD&days=" + days ;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch (HttpClientErrorException.TooManyRequests e) {
            System.out.println("Rate limit hit: " + e.getResponseBodyAsString());
            throw new Exception("Rate limit exceeded. Please try again later.");
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Invalid coinId: " + coinId);
            throw new Exception("Coin data not found for: " + coinId);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("API error: " + e.getResponseBodyAsString());
            throw new Exception("CoinGecko error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Unknown error: " + e.getMessage());
            throw new Exception("Unexpected server error: " + e.getMessage());
        }
    }

//    @Override
//    public String getCoinDetails(String coinId) throws Exception {
//        String url = "https://api.coingecko.com/api/v3/coins/"+coinId;
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        try{
//            HttpHeaders headers = new HttpHeaders();
//            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);
//
//            JsonNode jsonNode = objectMapper.readTree(response.getBody());
//            Coin coin = new Coin();
//            coin.setId(jsonNode.get("id").asText());
//            coin.setName(jsonNode.get("name").asText());
//            coin.setSymbol(jsonNode.get("symbol").asText());
//            coin.setImage(jsonNode.get("image").get("large").asText());
//
//           JsonNode marketData = jsonNode.get("market_data");
//
//            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
//            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
//            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt(0));
//            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
//            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
//            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
//            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
//            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
//
//            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
//
//            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asLong());
//
//            coin.setTotalSupply(marketData.get("total_supply").asLong());
//            coinRepository.save(coin);
//            return response.getBody();
//        }
//        catch (HttpClientErrorException | HttpServerErrorException e){
//            System.out.println("Error: " + e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }

    private String getSafeText(JsonNode node, String key) {
        return node.hasNonNull(key) ? node.get(key).asText() : null;
    }

    private double getSafeDouble(JsonNode node, String key) {
        return node.hasNonNull(key) ? node.get(key).asDouble() : 0.0;
    }

    private long getSafeLong(JsonNode node, String key) {
        return node.hasNonNull(key) ? node.get(key).asLong() : 0L;
    }

    private int getSafeInt(JsonNode node, String key) {
        return node.hasNonNull(key) ? node.get(key).asInt() : 0;
    }
    @Override
    @Cacheable(value = "coinDetails", key = "#coinId", unless = "#result == null")
    public String getCoinDetails(String coinId) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            Coin coin = new Coin();

            coin.setId(getSafeText(jsonNode, "id"));
            coin.setName(getSafeText(jsonNode, "name"));
            coin.setSymbol(getSafeText(jsonNode, "symbol"));

            JsonNode imageNode = jsonNode.path("image");
            coin.setImage(getSafeText(imageNode, "large"));

            JsonNode marketData = jsonNode.path("market_data");

            coin.setCurrentPrice(getSafeDouble(marketData.path("current_price"), "usd"));
            coin.setMarketCap(getSafeLong(marketData.path("market_cap"), "usd"));
            coin.setMarketCapRank(getSafeInt(jsonNode, "market_cap_rank"));  // Note: market_cap_rank is at root level in some cases
            coin.setTotalVolume(getSafeLong(marketData.path("total_volume"), "usd"));
            coin.setHigh24h(getSafeDouble(marketData.path("high_24h"), "usd"));
            coin.setLow24h(getSafeDouble(marketData.path("low_24h"), "usd"));
            coin.setPriceChange24h(getSafeDouble(marketData, "price_change_24h"));
            coin.setPriceChangePercentage24h(getSafeDouble(marketData, "price_change_percentage_24h"));
            coin.setMarketCapChange24h(getSafeLong(marketData, "market_cap_change_24h"));
            coin.setMarketCapChangePercentage24h(getSafeDouble(marketData, "market_cap_change_percentage_24h"));
            coin.setTotalSupply(getSafeLong(marketData, "total_supply"));

            coinRepository.save(coin);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("API error: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    @Override
    public Coin findCoinById(String coinId) throws Exception {
        Optional<Coin> optionalCoin = coinRepository.findById(coinId);
        if(optionalCoin.isEmpty()) throw new Exception("Coin not found.");
        return optionalCoin.get();
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        String url = "https://api.coingecko.com/api/v3/search?query="+keyword;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "top50Coins", unless = "#result == null")
    public String getTop50CoinsByMarketCapRank() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=1";
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String getTradingCoins() throws Exception {
        String url = "https://api.coingecko.com/api/v3/search/trending";
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String >("parameters",headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "topGainers", unless = "#result == null")
    public List<Coin> getTopGainers() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=USD&per_page=250&page=1";
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<Coin> coinList = objectMapper.readValue(response.getBody(), new TypeReference<List<Coin>>() {});

            return coinList.stream()
                    .sorted((a, b) -> Double.compare(b.getPriceChangePercentage24h(), a.getPriceChangePercentage24h())) // descending
                    .limit(10)
                    .toList();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception("Error fetching top gainers: " + e.getMessage());
        }
    }


    @Override
    @Cacheable(value = "topLosers", unless = "#result == null")
    public List<Coin> getTopLosers() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=USD&per_page=250&page=1";
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            List<Coin> coinList = objectMapper.readValue(response.getBody(), new TypeReference<List<Coin>>() {});

            return coinList.stream()
                    .sorted((a, b) -> Double.compare(a.getPriceChangePercentage24h(), b.getPriceChangePercentage24h()))
                    .limit(10)
                    .toList();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception("Error fetching top losers: " + e.getMessage());
        }
    }
}
