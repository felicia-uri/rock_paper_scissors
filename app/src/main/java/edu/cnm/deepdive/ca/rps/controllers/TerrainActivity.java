package edu.cnm.deepdive.ca.rps.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.cnm.deepdive.ca.rps.R;
import edu.cnm.deepdive.ca.rps.models.Terrain;
import edu.cnm.deepdive.ca.rps.views.TerrainView;

/**
 * Activity class for Deadlock(Rock-Paper-Scissors) cellular automaton.
 * @author FeliciaUrioste
 */
public class TerrainActivity extends AppCompatActivity {

  /** Default rest duration of overridden run method of Thread class inside nested Runner class */
  private static final int RUNNER_THREAD_REST = 40;

  /** Default sleep duration of overridden run method of Thread class inside nested Runner class */
  private static final int RUNNER_THREAD_SLEEP = 50;


  private boolean running = false;
  private boolean inForeground = false;
  private Terrain terrain = null;
  private TerrainView terrainView = null;
  private View terrainLayout;
  private Runner runner = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terrain);
    initializeModel();
    initializeUserInterface();
  }

  @Override
  protected void onStart() {
    super.onStart();
    setInForeground(true);
  }

  @Override
  protected void onStop() {
    setInForeground(false);
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean running = isRunning();
    menu.findItem(R.id.run_item).setVisible(!running);
    menu.findItem(R.id.pause_item).setVisible(running);
    menu.findItem(R.id.reset_item).setEnabled(!running);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.run_item:
        setRunning(true);
        break;
      case R.id.pause_item:
        setRunning(false);
        break;
      case R.id.reset_item:
        setInForeground(false);
        initializeModel();
        setInForeground(true);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    invalidateOptionsMenu();
    return true;
  }

  /**
   * Instantiates a new Terrain object with a reference name terrain.
   * Calls the initialize() method inherited from Terrain model to create a lattice with
   * randomly assigned instance of Breed.
   */
  private void initializeModel() {
    terrain = new Terrain();
    terrain.initialize();
  }

  /**
   * Assign values for terrainLayout and terrainView objects.
   *
   */
  private void initializeUserInterface() {
    terrainLayout = findViewById(R.id.terrainLayout);
    terrainView = (TerrainView) findViewById(R.id.terrainView);
  }

  /**
   * Returns the currently specified value of running field.
   *
   * @return on or off flag
   */
  private synchronized boolean isRunning() {
    return running;
  }

  /**
   * Sets the value of running to be used by the Runner class to determine whether to run
   * terrain.step() and terrainView.setSource() methods.
   *
   * Allows the View to be updated or not based on the two conditions specified in
   * the while loop inside the Runner class.
   *
   * @param running on or off flag
   */
  private synchronized void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * Returns the currently specified value of inForeground field based whether the app is paused,
   * running or resetting.
   *
   * @return on or off flag
   */
  private synchronized boolean isInForeground() {
    return inForeground;
  }

  /**
   * Sets the value of inForeground to be used as a flag to determine whether to reset, pause, or
   * run the app.
   *
   * @param inForeground on or off flag
   */
  private synchronized void setInForeground(boolean inForeground) {
    if (inForeground) {
      this.inForeground = true;
      if (runner == null) {
        runner = new Runner();
        runner.start();
      }
      terrainLayout.post(new Runnable() {
        @Override
        public void run() {
          terrainView.setSource(terrain.getSnapshot());
        }
      });
    } else {
      this.inForeground = false;
      runner = null;
    }
  }

  // TODO - Understand what the Runner class is doing
  private class Runner extends Thread {

    @Override
    public void run() {
      while (isInForeground()) {
        while (isRunning() && isInForeground()) {
          terrain.step();
          terrainView.setSource(terrain.getSnapshot());
          try {
            Thread.sleep(RUNNER_THREAD_REST);
          } catch (InterruptedException ex) {
            // Do nothing.
          }
        }
        try {
          Thread.sleep(RUNNER_THREAD_SLEEP);
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      }
    }
  }

}

