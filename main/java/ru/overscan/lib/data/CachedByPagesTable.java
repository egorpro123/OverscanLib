package ru.overscan.lib.data;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;
import android.util.SparseArray;

public class CachedByPagesTable extends Table{
	final String TAG = "CachedByPagesTable";
	public int totalPages;
	public int recsInPage;
	public int currentPage;
	public int prevCurrentPage;
	public int maxCachedPages;
	public int cachedPagesAfterLast;
	public SparseArray<Data> cache;
	public ArrayList<Integer> pagesInCache;
//	public Queue<Integer> pagesInCache;
//	public int size;
	
	public int maxPages;
	public boolean wasMaxCount;
	public boolean reachedEnd;
	public int maxSize;
	
	public CachedByPagesTable() {
		this(50);
	}
	
	public CachedByPagesTable(int recsInPage) {
		super();
		this.recsInPage = recsInPage;
		totalPages = 1;
		prevCurrentPage = -1;
		currentPage = 0;
//		maxCachedPages = 10;
		maxCachedPages = 2;
		cache = new SparseArray<Data>();
		cache.put(currentPage, currentDatas);
		pagesInCache = new ArrayList<Integer>();
		pagesInCache.add(currentPage);
		cachedPagesAfterLast = 1;
		reachedEnd = false;
		maxSize = 0;
//		size = 0;
	}
	
	public void checkStateBeforeAddRec() {
//		Log.d(TAG, "currentSize " + currentSize);
//		Log.d(TAG, "totalPages " + totalPages);
		if (currentSize == recsInPage) {
			addPage();
		}
	}
	
	public void checkCachedPagesAmount() {
//		Log.d(TAG, "pagesInCache.size() " + pagesInCache.size());
		if (pagesInCache.size() > maxCachedPages) {
			int pagesToRemove = pagesInCache.size() - maxCachedPages;
			int diff = -1, d, page = -1;
			for (int i=0; i < pagesToRemove; i++) {
				for(int j: pagesInCache)
					if (j != currentPage) {
						d = Math.abs(j - currentPage);
						if (d > diff) {
							diff = d;
							page = j;
						}
						else if (d == diff) {
							if (prevCurrentPage < currentPage) {
								if (j < currentPage) page = j;
							}
							else if (prevCurrentPage > currentPage) {
								if (j > currentPage) page = j;
							}
						}
					}
//				Log.d(TAG, "removing " + page);
				cache.remove(page);
				pagesInCache.remove(Integer.valueOf(page));
			}
		}
//				for (int j = 0; j < pagesInCache.size(); j++)
//				
//				pagesInCache.toArray(contents)
				
//				int j = pagesInCache.poll();
//				cache.remove(j);
//				Log.d(TAG, "poll() " + j);			
//			}
//		}
//		Log.d(TAG, "- pagesInCache.size() after " + pagesInCache.size());
	}
	
	public int getFirstRecIndexOnPage(int page) {
		return page * recsInPage;
	}

	
	public int getLastRecIndexOnPage(int page) {
		return page * recsInPage + recsInPage - 1;
	}
	
	public boolean existPage(int page) {
		return pagesInCache.contains(page);
	}
	
	public class NotCachedPages{
		public LinkedList<Integer> needed;
		public LinkedList<Integer> desirable;	
		
//		public NotCachedPages() {
//			needed = new LinkedList<Integer>();
//			desirable = new LinkedList<Integer>();	
//		}
		
		public void addNeeded(int page){
			if (needed == null) needed = new LinkedList<Integer>(); 
			needed.add(page);
		}
		
		public void addDesirable(int page){
			if (desirable == null) desirable = new LinkedList<Integer>(); 
			desirable.add(page);
		}
	}
	
	public NotCachedPages notCachedPagesForRecs(int firstRec, int lastRec) {
		if (firstRec > lastRec) return null;
		NotCachedPages pages = new NotCachedPages();
		int firstPage = whatPage(firstRec);
		int lastPage = whatPage(lastRec);
		for (int i = firstPage; i <= lastPage + cachedPagesAfterLast; i++) {
			if (!existPage(i)) 
				if (i > lastPage) pages.addDesirable(i);
				else pages.addNeeded(i);
		}
		return pages;
	}
	
	
	protected void addPage(){
		currentDatas = this.new Data();
		cache.put(totalPages, currentDatas);
		pagesInCache.add(totalPages);
		currentSize = 0;
//		prevCurrentPage = currentPage;
//		currentPage = totalPages; 
		setCurrentPage(totalPages);
		totalPages++;
		checkCachedPagesAmount();
	}
	
	public void setCurrentPage(int page) {
		prevCurrentPage = currentPage;
		currentPage = page; 		
	}
	
	@Override
	public int size() {
		if (reachedEnd) {
			return maxSize;
		}
		// если конец не достигнут
		else return (totalPages + 1) * recsInPage; 
	}
	
	public void setReachedEnd() {
		reachedEnd = true;
	}
	
	@Override
	public void addRec() {
		checkStateBeforeAddRec();
		maxSize++;
		super.addRec();
	}

	protected void setPageCurrent(int page){
		if (pagesInCache.contains(page)) chooseCachedPage(page);
		else if (page == totalPages) addPage();
		else {
//			prevCurrentPage = currentPage;
//			currentPage = page;
			setCurrentPage(page);
			currentDatas = this.new Data();
			cache.put(currentPage, currentDatas);
			pagesInCache.add(currentPage);
			currentSize = 0;
			checkCachedPagesAmount();
		}
	}

	
	public void setRec(int pos) {
		int page = whatPage(pos);
		int pagePos = whatPagePos(pos);
		if (page != currentPage) setPageCurrent(page);
		if (maxSize < pos+1) maxSize = pos+1;
		super.setRec(pagePos);
	}

	
	public int whatPage(int pos) {
		return (pos / recsInPage);
	}

	
	public int whatPagePos(int pos) {
//		int page = (pos + 1) / recsInPage;
		return pos % recsInPage;
	}
	
	public boolean chooseCachedPage(int page) {
		if (page + 1 > totalPages || !pagesInCache.contains(page)) return false;
		else {
			if (page != currentPage) {
				currentDatas = cache.get(page);
				setCurrentPage(page);
//				prevCurrentPage = currentPage;
//				currentPage = page;
				if (currentPage < totalPages-1) currentSize = recsInPage;
				else currentSize = whatPagePos(size()-1) + 1; 
			}
			return true;
		}
	}
	
	@Override
	public void chooseRec(int pos) {
		int page = whatPage(pos);
		if (chooseCachedPage(page)) {
			super.chooseRec(whatPagePos(pos));
		}
		else buffer.clear();
	}
	
	public Record getDatas(int pos) {
		chooseRec(pos);
		return buffer;
	}

	public String[] getRecAsArray(int pos) {
		int page = whatPage(pos);
		if (chooseCachedPage(page)) {
			return super.getRecAsArray(whatPagePos(pos));
		}
		else return null;
	}
	
	public String[] getRecPrimaryKey(int pos) {
		if (primaryKey == null) return null;
		chooseRec(pos);
		String[] key = new String[primaryKey.length];
		for (int i = 0; i < primaryKey.length; i++) {
			key[i] = get(primaryKey[i]);
		}
		return key;
	}
	
	
	public String getFieldAt(String field, int pos) {
		chooseRec(pos);
		return get(field);
	};
		
}

// - test -
//CachedByPagesTable table = new CachedByPagesTable(2);
//table. = 2;
//table.addField("f1");
//table.addField("f2");
//int total = 8;
//for(int i=0; i<total; i++) {
//	table.put("f1", "field f1, rec " + i);
//	table.put("f2", "field f2, rec " + i);
//	table.saveRec();
//}
//showText("size " + table.size());
//showText("totalPages " + table.totalPages);
//showText("currentPage " + table.currentPage);
//for(int i = 0; i < total; i++) {
//	table.chooseRec(i);
//	showText("rec" + i + " " + table.get("f1") + " " + table.get("f2"));
//}
//CachedByPagesTable.NotCachedPages pages = table.existRecsPages(0, 5);
//showText("needed pages " + 
//		Arrays.toString(pages.needed.toArray(new Integer[0])));
//showText("desirable pages " + 
//		Arrays.toString(pages.desirable.toArray(new Integer[0])));

