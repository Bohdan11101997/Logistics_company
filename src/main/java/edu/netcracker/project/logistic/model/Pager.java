package edu.netcracker.project.logistic.model;

import java.util.Optional;

public class Pager {

    public static final int INITIAL_PAGE_SIZE = 20;
    public static final int INITIAL_BUTTONS_TO_SHOW = 5;
    public static final int INITIAL_PAGE = 0;
    public static final int[] ITEMS_ON_PAGE_VALUES = {5, 10, 20, 50};
    public static final int[] BUTTONS_TO_SHOW_VALUES = {5, 7, 9};

    // first and last page numbers
    private int startPage;
    private int endPage;

    // Current parameters
    private int itemsOnPage;
    private int currentPage;
    private int buttonsToShow;

    // global parameters
    private int totalPages;

    public Pager(int allItemsCount, Optional<Integer> itemsOnPageOptional, Optional<Integer> currentPageOptional, Optional<Integer> buttonsToShowOptional) {

        itemsOnPage = itemsOnPageOptional.orElse(Pager.INITIAL_PAGE_SIZE);
        currentPage = (currentPageOptional.orElse(0) < 1) ? Pager.INITIAL_PAGE : currentPageOptional.get()-1;
        buttonsToShow = buttonsToShowOptional.orElse(Pager.INITIAL_BUTTONS_TO_SHOW);

        setTotalPages(allItemsCount);
        setButtonsToShow();
        setStartAndEndPages();
    }

    private void setTotalPages(int allItemsCount) {
        totalPages = allItemsCount/itemsOnPage;
        if (allItemsCount % itemsOnPage != 0){
            totalPages++;
        }
    }

    private void setButtonsToShow() {
        if (buttonsToShow % 2 != 0) {
            this.buttonsToShow = buttonsToShow;
        } else {
            this.buttonsToShow = INITIAL_BUTTONS_TO_SHOW;
        }
    }

    private void setStartAndEndPages() {

        int halfPagesToShow = getButtonsToShow() / 2;

        if (totalPages <= getButtonsToShow()){
            setStartPage(1);
            setEndPage(totalPages);
        }else if (currentPage - halfPagesToShow <= 0) {
            setStartPage(1);
            setEndPage(getButtonsToShow());

        } else if (currentPage + halfPagesToShow == totalPages) {
            setStartPage(currentPage - halfPagesToShow);
            setEndPage(totalPages);

        } else if (currentPage + halfPagesToShow > totalPages) {
            setStartPage(totalPages - getButtonsToShow() + 1);
            setEndPage(totalPages);

        } else {
            setStartPage(currentPage - halfPagesToShow);
            setEndPage(currentPage + halfPagesToShow);
        }
    }

    public int getButtonsToShow(){
        return buttonsToShow;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getItemsOnPage() {
        return itemsOnPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
