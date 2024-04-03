package com.example.toyprojectback.entity;




public enum BoardCategory {
    FREE,GREETING, GOLD;

    public static BoardCategory of(String category) {
        if(category.equals("FREE")) return BoardCategory.FREE;
        else if(category.equals("GREETING")) return BoardCategory.GREETING;
        else if(category.equals("GOLD")) return BoardCategory.GOLD;
        else return null;
    }



    //of() 용도 : DB에서 받아온 String 값을 Enum으로 변환

}