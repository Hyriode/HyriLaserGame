package fr.hyriode.lasergame.utils;

import java.util.Objects;

public interface TriConsumer<T, U, K> {

    void accept(T t, U u, K k);

    default TriConsumer<T, U, K> andThen(TriConsumer<? super T, ? super U, ? super K> after) {
        Objects.requireNonNull(after);

        return (l, r, k) -> {
            accept(l, r, k);
            after.accept(l, r, k);
        };
    }
}
