package com.tradetrack.cryptolist.dataModel;

import com.google.gson.annotations.SerializedName;

public class CoinModel {
    private String name;
    private String symbol;
    private int rank;
    private int age;
    private String color;
    @SerializedName("png32")
    private String png32Url;
    @SerializedName("png64")
    private String png64Url;
    @SerializedName("webp32")
    private String webp32Url;
    @SerializedName("webp64")
    private String webp64Url;
    private double exchanges;
    private double markets;
    private double pairs;
    private Object[] categories; // Assuming it's an array of objects
    private double allTimeHighUSD;
    private double circulatingSupply;
    private double totalSupply;
    private double maxSupply;
    private Links links; // Map to hold links data

    public class Links {
        private String website;
        private String whitepaper;
        private String twitter;
        private String reddit;
        private String telegram;
        private String discord;
        private String medium;
        private String instagram;
        private String tiktok;
        private String youtube;
        private String linkedin;
        private String twitch;
        private String spotify;
        private String naver;
        private String wechat;
        private String soundcloud;

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getWhitepaper() {
            return whitepaper;
        }

        public void setWhitepaper(String whitepaper) {
            this.whitepaper = whitepaper;
        }

        public String getTwitter() {
            return twitter;
        }

        public void setTwitter(String twitter) {
            this.twitter = twitter;
        }

        public String getReddit() {
            return reddit;
        }

        public void setReddit(String reddit) {
            this.reddit = reddit;
        }

        public String getTelegram() {
            return telegram;
        }

        public void setTelegram(String telegram) {
            this.telegram = telegram;
        }

        public String getDiscord() {
            return discord;
        }

        public void setDiscord(String discord) {
            this.discord = discord;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getInstagram() {
            return instagram;
        }

        public void setInstagram(String instagram) {
            this.instagram = instagram;
        }

        public String getTiktok() {
            return tiktok;
        }

        public void setTiktok(String tiktok) {
            this.tiktok = tiktok;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        public String getLinkedin() {
            return linkedin;
        }

        public void setLinkedin(String linkedin) {
            this.linkedin = linkedin;
        }

        public String getTwitch() {
            return twitch;
        }

        public void setTwitch(String twitch) {
            this.twitch = twitch;
        }

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

        public String getNaver() {
            return naver;
        }

        public void setNaver(String naver) {
            this.naver = naver;
        }

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        public String getSoundcloud() {
            return soundcloud;
        }

        public void setSoundcloud(String soundcloud) {
            this.soundcloud = soundcloud;
        }
    }
    private String code;
    private double rate;
    private long volume;
    private long cap;
    private Delta delta; // Delta object for hour, day, week, month, quarter, year


    public class Delta {
        private double hour;
        private double day;
        private double week;
        private double month;
        private double quarter;
        private double year;

        public double getHour() {
            return hour;
        }

        public void setHour(double hour) {
            this.hour = hour;
        }

        public double getDay() {
            return day;
        }

        public void setDay(double day) {
            this.day = day;
        }

        public double getWeek() {
            return week;
        }

        public void setWeek(double week) {
            this.week = week;
        }

        public double getMonth() {
            return month;
        }

        public void setMonth(double month) {
            this.month = month;
        }

        public double getQuarter() {
            return quarter;
        }

        public void setQuarter(double quarter) {
            this.quarter = quarter;
        }

        public double getYear() {
            return year;
        }

        public void setYear(double year) {
            this.year = year;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPng32Url() {
        return png32Url;
    }

    public void setPng32Url(String png32Url) {
        this.png32Url = png32Url;
    }

    public String getPng64Url() {
        return png64Url;
    }

    public void setPng64Url(String png64Url) {
        this.png64Url = png64Url;
    }

    public String getWebp32Url() {
        return webp32Url;
    }

    public void setWebp32Url(String webp32Url) {
        this.webp32Url = webp32Url;
    }

    public String getWebp64Url() {
        return webp64Url;
    }

    public void setWebp64Url(String webp64Url) {
        this.webp64Url = webp64Url;
    }

    public double getExchanges() {
        return exchanges;
    }

    public void setExchanges(double exchanges) {
        this.exchanges = exchanges;
    }

    public double getMarkets() {
        return markets;
    }

    public void setMarkets(double markets) {
        this.markets = markets;
    }

    public double getPairs() {
        return pairs;
    }

    public void setPairs(double pairs) {
        this.pairs = pairs;
    }

    public Object[] getCategories() {
        return categories;
    }

    public void setCategories(Object[] categories) {
        this.categories = categories;
    }

    public double getAllTimeHighUSD() {
        return allTimeHighUSD;
    }

    public void setAllTimeHighUSD(double allTimeHighUSD) {
        this.allTimeHighUSD = allTimeHighUSD;
    }

    public double getCirculatingSupply() {
        return circulatingSupply;
    }

    public void setCirculatingSupply(double circulatingSupply) {
        this.circulatingSupply = circulatingSupply;
    }

    public double getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(double totalSupply) {
        this.totalSupply = totalSupply;
    }

    public double getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(double maxSupply) {
        this.maxSupply = maxSupply;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getCap() {
        return cap;
    }

    public void setCap(long cap) {
        this.cap = cap;
    }

    public Delta getDelta() {
        return delta;
    }

    public void setDelta(Delta delta) {
        this.delta = delta;
    }
}
