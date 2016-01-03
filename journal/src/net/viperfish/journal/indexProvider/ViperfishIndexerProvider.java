package net.viperfish.journal.indexProvider;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Provider;
import net.viperfish.utils.index.Indexer;

public class ViperfishIndexerProvider implements Provider<Indexer<Journal>> {

	private JournalIndexerFactory indexer;

	public ViperfishIndexerProvider() {
		indexer = new JournalIndexerFactory();
	}

	@Override
	public Indexer<Journal> newInstance() {
		return indexer.createIndexer();
	}

	@Override
	public Indexer<Journal> getInstance() {
		return indexer.getIndexer();
	}

	@Override
	public Indexer<Journal> newInstance(String instance) {
		if (instance.equals("LuceneIndexer")) {
			return indexer.createIndexer();
		}
		return null;
	}

	@Override
	public Indexer<Journal> getInstance(String instance) {
		if (instance.equals("LuceneIndexer")) {
			return indexer.getIndexer();
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "LuceneIndexer" };
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultInstance(String instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultInstance() {
		return "LuceneIndexer";
	}

}
