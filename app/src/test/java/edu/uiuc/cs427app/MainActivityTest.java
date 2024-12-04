package edu.uiuc.cs427app;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Intent;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MainActivityTest {

    private MainActivity mainActivity;
    private FirebaseFirestore mockFirestore;
    private LinearLayout mockLocationsContainer;

    @Before
    public void setUp() {
        // Mock Firestore and other dependencies
        mockFirestore = Mockito.mock(FirebaseFirestore.class);
        mockLocationsContainer = Mockito.mock(LinearLayout.class);

        // Create the MainActivity instance with mock context
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("username", "testuser");
        mainActivity = new MainActivity();
        mainActivity.setIntent(intent);

        // Inject the mocked Firestore instance into MainActivity
        mainActivity.db = mockFirestore;
        mainActivity.locationsContainer = mockLocationsContainer;
    }

    @Test
    public void testAddCityToDatabase_Success() {
        // Mock a successful Firestore query
        QuerySnapshot mockSnapshot = Mockito.mock(QuerySnapshot.class);
        when(mockSnapshot.isEmpty()).thenReturn(false);
        when(mockSnapshot.getDocuments()).thenReturn(List.of(Mockito.mock(DocumentSnapshot.class)));

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(mockFirestore).collection(anyString()).whereEqualTo(anyString(), any()).get();

        // Call the method
        mainActivity.addCityToDatabase("Test City");

        // Verify that Firestore's update method was called with the correct parameters
        verify(mockFirestore.collection("users").document(anyString()), times(1))
                .update(eq("locations"), any());
    }

    @Test
    public void testAddCityToDatabase_Failure() {
        // Mock a failed Firestore query
        when(mockFirestore.collection(anyString()).whereEqualTo(anyString(), any()).get())
                .thenThrow(new RuntimeException("Failed to fetch data"));

        // Call the method
        mainActivity.addCityToDatabase("Test City");

        // Assert that the failure case is handled
        verify(mockFirestore, times(0)).collection("users").document(anyString());
    }

    @Test
    public void testRemoveCityFromDatabase_Success() {
        // Mock a successful Firestore query
        QuerySnapshot mockSnapshot = Mockito.mock(QuerySnapshot.class);
        when(mockSnapshot.isEmpty()).thenReturn(false);
        when(mockSnapshot.getDocuments()).thenReturn(List.of(Mockito.mock(DocumentSnapshot.class)));

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).when(mockFirestore).collection(anyString()).whereEqualTo(anyString(), any()).get();

        // Call the method
        mainActivity.removeCityFromDatabase("Test City");

        // Verify that Firestore's update method was called with the correct parameters
        verify(mockFirestore.collection("users").document(anyString()), times(1))
                .update(eq("locations"), any());
    }

    @Test
    public void testRemoveCityFromDatabase_Failure() {
        // Mock a failed Firestore query
        when(mockFirestore.collection(anyString()).whereEqualTo(anyString(), any()).get())
                .thenThrow(new RuntimeException("Failed to fetch data"));

        // Call the method
        mainActivity.removeCityFromDatabase("Test City");

        // Assert that the failure case is handled
        verify(mockFirestore, times(0)).collection("users").document(anyString());
    }
}
