package fr.zigomar.chroma.chroma.adapters.modeladapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.zigomar.chroma.chroma.model.Book;
import fr.zigomar.chroma.chroma.R;

public class BooksAdapter extends ArrayAdapter<Book> {

    public BooksAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unit_book, parent, false);
        }

        BookViewHolder viewHolder = (BookViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BookViewHolder();
            viewHolder.title = convertView.findViewById(R.id.book_title);
            viewHolder.author = convertView.findViewById(R.id.book_author);
            viewHolder.date_open = convertView.findViewById(R.id.book_dateopen);
            viewHolder.review = convertView.findViewById(R.id.book_review);
            viewHolder.date_closed = convertView.findViewById(R.id.book_dateclosed);
            convertView.setTag(viewHolder);
        }

        Book book = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        assert book != null;
        viewHolder.title.setText(book.getTitle());
        viewHolder.author.setText(book.getAuthor());
        viewHolder.date_open.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(book.getDateOpen()));

        if (book.getFinished()) {
            viewHolder.date_closed.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE).format(book.getDateFinished()));
        }

        if (book.hasReview()) {
            String review_str = book.getReview() + "...";
            viewHolder.review.setText(review_str);
        }

        return convertView;
    }

    private class BookViewHolder{
        TextView title;
        TextView author;
        TextView date_open;
        TextView review;
        TextView date_closed;
    }
}
