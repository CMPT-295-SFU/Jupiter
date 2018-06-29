/*
Copyright (C) 2018 Andres Castellanos

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>
*/

package vsim.utils;

import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;


/**
 * The class ArgumentParser represents a useful command line argument parser.
 */
final class ArgumentParser {

  /** stores the max length of all options */
  private int maxLength;
  /** stores the flags that were set */
  private HashMap<String, Integer> flags;
  /** stores the values that were set */
  private HashMap<Integer, String> values;
  /** stores the valid options for this parser */
  private TreeMap<String, String> options;
  /** stores the valid options that requires a value */
  private HashSet<String> optionsWithVal;
  /** stores all the targers (aka files) */
  private ArrayList<String> targets;
  /** stores all the parser errors */
  private ArrayList<String> errors;

  /**
   * Unique constructor that initializes a newly ArgumentParser object.
   */
  protected ArgumentParser() {
    this.maxLength = -1;
    this.flags = new HashMap<String, Integer>();
    this.values = new HashMap<Integer, String>();
    this.options = new TreeMap<String, String>();
    this.optionsWithVal = new HashSet<String>();
    this.targets = new ArrayList<String>();
    this.errors = new ArrayList<String>();
  }

  /**
   * This method adds a new option to the valid option list.
   *
   * @param option the option that starts with -
   * @param help help information attached to this option
   * @param requiresValue if this option requires a value
   */
  protected void add(String option, String help, boolean requiresValue) {
    // only include options that starts with '-'
    if (option != null && option.startsWith("-")) {
      this.options.put(option, help);
      // this option requires a value
      if (requiresValue) {
        this.optionsWithVal.add(option);
        this.maxLength = Math.max(this.maxLength, option.length() * 2 + 2);
      } else
        this.maxLength = Math.max(this.maxLength, option.length() + 2);
    }
  }

  /**
   * This method adds a new option that does not requires a value to
   * the valid option list.
   *
   * @param option the option that starts with -
   * @param help help information attached to this option
   */
  protected void add(String option, String help) {
    this.add(option, help, false);
  }

  /**
   * This method parses all command line arguments, verifies errors
   * (if any) and stores all useful information.
   *
   * @param args command line arguments
   */
  protected void parse(String[] args) {
    // clear old contents
    this.flags.clear();
    this.values.clear();
    this.targets.clear();
    this.errors.clear();
    System.gc();
    // examine all arguments
    String lastFlag = null;
    int lastFlagPos = -1;
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        // localize last flag
        lastFlagPos = i;
        lastFlag = args[i];
        // valid flag ?
        if (this.options.containsKey(args[i]))
          this.flags.put(args[i], i);
        else
          this.errors.add("unknown argument: " + args[i]);
      } else
        // store every value
        this.values.put(i, args[i]);
    }
    // verify values
    for (String flag: this.flags.keySet()) {
      int position = this.flags.get(flag);
      // no value is present if needed?
      String value = this.values.get(position + 1);
      if (value == null && this.optionsWithVal.contains(flag))
        this.errors.add("argument '" + flag + "' requires a value");
      // examine for unexpected values
      if (position != lastFlagPos && value != null) {
        String invalid = "";
        if (!this.optionsWithVal.contains(flag))
          invalid += value + " ";
        for (int i = position + 2; this.values.get(i) != null; i++)
          invalid += this.values.get(i) + " ";
        if (!invalid.equals(""))
          this.errors.add("unexpected value(s): " + invalid.trim());
      }
    }
    // set targets
    if (lastFlag != null) {
      int offset = lastFlagPos;
      if (this.optionsWithVal.contains(lastFlag))
        offset += 2;
      else
        offset += 1;
      // add targets starting at this offset
      for (int i = offset; this.values.get(i) != null; i++)
        this.targets.add(this.values.get(i));
    } else {
      // targets = all values
      for (int pos: this.values.keySet())
        this.targets.add(this.values.get(pos));
    }
  }

  /**
   * This method pretty prints the argument parser usage.
   */
  protected void print() {
    String newline = System.getProperty("line.separator");
    String out = "usage: vsim [options] <files>" + newline + newline;
    out += "available options:" + newline;
    for (String option: this.options.keySet()) {
      out += "  " + option;
      int length = option.length();
      if (this.optionsWithVal.contains(option)) {
        out += " " + option.substring(1).toUpperCase();
        length += option.length();
      }
      for (int i = 0; i < (this.maxLength - length); i++)
        out += " ";
      out += this.options.get(option) + newline;
    }
    System.out.println(out.trim());
  }

  /**
   * This method verifies if a flag was set.
   *
   * @return true if flag was set, false if not
   */
  protected boolean hasFlag(String flag) {
    return this.flags.containsKey(flag);
  }

  /**
   * This method returns the value attached to a flag (if any)
   */
  protected String value(String flag) {
    if (this.options.containsKey(flag) && this.optionsWithVal.contains(flag))
      return this.values.get(this.flags.get(flag) + 1);
    return null;
  }

  /**
   * This method is useful to verify if the argument parser has errors.
   *
   * @return true if the parser has errors, false otherwise
   */
  protected boolean hasErrors() {
    return this.errors.size() > 0;
  }

  /**
   * This method returns the list of errors (if any).
   *
   * @return list of errors
   */
  protected ArrayList<String> getErrors() {
    this.errors.trimToSize();
    return this.errors;
  }

  /**
   * This method returns all the targets (aka files).
   *
   * @return array of targets
   */
  protected ArrayList<String> targets() {
    return this.targets;
  }

}