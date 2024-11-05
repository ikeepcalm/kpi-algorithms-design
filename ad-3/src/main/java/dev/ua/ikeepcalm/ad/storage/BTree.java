package dev.ua.ikeepcalm.ad.storage;

import dev.ua.ikeepcalm.ad.entities.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BTree {

    private int numberOfLastComparisons = 0;
    private static final String FILE_PATH = "users.dat";
    private BNode root;
    private final int t;

    public BTree(int t) {
        this.t = t;
        this.root = new BNode(t, true);

        List<User> users = readUsersFromFile();
        for (User user : users) {
            insert(user);
        }
    }

    private int binarySearch(BNode node, int id) {
        int low = 0, high = node.keys.size() - 1;
        while (low <= high) {
            numberOfLastComparisons++;
            int mid = (low + high) / 2;
            int cmp = Integer.compare(id, node.keys.get(mid).getId());
            if (cmp == 0) {
                return mid;
            }
            if (cmp < 0) high = mid - 1;
            else low = mid + 1;
        }
        return low;
    }

    private void delete(BNode node, int id) {
        int idx = binarySearch(node, id);

        if (idx < node.keys.size() && node.keys.get(idx).getId() == id) {
            if (node.leaf) {
                node.keys.remove(idx);
            } else {
                deleteFromInternalNode(node, idx);
            }
        } else {
            if (node.leaf) {
                return;
            }

            boolean flag = (idx == node.keys.size());
            if (node.children.get(idx).keys.size() < t) {
                fill(node, idx);
            }

            if (flag && idx >= node.keys.size()) {
                delete(node.children.get(idx - 1), id);
            } else {
                delete(node.children.get(idx), id);
            }
        }
    }

    public void delete(int id) {
        delete(root, id);

        if (root.keys.isEmpty()) {
            if (!root.leaf) {
                root = root.children.getFirst();
            } else {
                root = null;
            }
        }

        writeUsersToFile(getAllUsers(root));
    }

    private void deleteFromInternalNode(BNode node, int idx) {
        User key = node.keys.get(idx);

        if (node.children.get(idx).keys.size() >= t) {
            User pred = getPredecessor(node, idx);
            node.keys.set(idx, pred);
            delete(node.children.get(idx), pred.getId());
        } else if (node.children.get(idx + 1).keys.size() >= t) {
            User succ = getSuccessor(node, idx);
            node.keys.set(idx, succ);
            delete(node.children.get(idx + 1), succ.getId());
        } else {
            merge(node, idx);
            delete(node.children.get(idx), key.getId());
        }
    }

    private User getPredecessor(BNode node, int idx) {
        BNode cur = node.children.get(idx);
        while (!cur.leaf) {
            cur = cur.children.get(cur.keys.size());
        }
        return cur.keys.getLast();
    }

    private User getSuccessor(BNode node, int idx) {
        BNode cur = node.children.get(idx + 1);
        while (!cur.leaf) {
            cur = cur.children.getFirst();
        }
        return cur.keys.getFirst();
    }

    private void fill(BNode node, int idx) {
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= t) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= t) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(BNode node, int idx) {
        BNode child = node.children.get(idx);
        BNode sibling = node.children.get(idx - 1);

        child.keys.addFirst(node.keys.get(idx - 1));
        if (!child.leaf) {
            child.children.addFirst(sibling.children.removeLast());
        }

        node.keys.set(idx - 1, sibling.keys.removeLast());
        assert child.keys.size() >= t - 1 && sibling.keys.size() >= t - 1;
    }

    private void borrowFromNext(BNode node, int idx) {
        BNode child = node.children.get(idx);
        BNode sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));
        if (!child.leaf) {
            child.children.add(sibling.children.removeFirst());
        }

        node.keys.set(idx, sibling.keys.removeFirst());
        assert child.keys.size() >= t - 1 && sibling.keys.size() >= t - 1;
    }


    private void merge(BNode node, int idx) {
        BNode child = node.children.get(idx);
        BNode sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.remove(idx));
        child.keys.addAll(sibling.keys);

        if (!child.leaf) {
            child.children.addAll(sibling.children);
        }

        node.children.remove(idx + 1);
    }

    public void update(User updatedUser) {
        User existingUser = search(updatedUser.getId());
        if (existingUser != null) {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setPassword(updatedUser.getPassword());
            writeUsersToFile(getAllUsers(root));
        }
    }

    public User search(int id) {
        return search(root, id);
    }

    private User search(BNode node, int id) {
        int i = binarySearch(node, id);
        if (i < node.keys.size() && node.keys.get(i).getId() == id) {
            return node.keys.get(i);
        }
        if (node.leaf) return null;

        return search(node.children.get(i), id);
    }

    public void insert(User user) {
        if (root.keys.size() == 2 * t - 1) {
            BNode newRoot = new BNode(t, false);
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        int newId = getMaxUserId() + 1;
        user.setId(newId);
        insertNonFull(root, user);
        appendUserToFile(user);
    }

    private int getMaxUserId() {
        int maxId = 0;
        List<User> users = getAllUsers(root);
        for (User user : users) {
            maxId = Math.max(maxId, user.getId());
        }
        return maxId;
    }


    private void insertNonFull(BNode node, User user) {
        if (node.leaf) {
            int insertPos = binarySearch(node, user.getId());
            node.keys.add(insertPos, user);
        } else {
            int childIdx = binarySearch(node, user.getId());
            if (node.children.get(childIdx).keys.size() == 2 * t - 1) {
                splitChild(node, childIdx);
                if (user.getId() > node.keys.get(childIdx).getId()) {
                    childIdx++;
                }
            }
            insertNonFull(node.children.get(childIdx), user);
        }
    }

    private void splitChild(BNode parent, int i) {
        BNode fullNode = parent.children.get(i);
        BNode newNode = new BNode(t, fullNode.leaf);

        newNode.keys.addAll(fullNode.keys.subList(t, 2 * t - 1));
        fullNode.keys.subList(t, 2 * t - 1).clear();

        if (!fullNode.leaf) {
            newNode.children.addAll(fullNode.children.subList(t, 2 * t));
            fullNode.children.subList(t, 2 * t).clear();
        }

        parent.keys.add(i, fullNode.keys.remove(t - 1));
        parent.children.add(i + 1, newNode);

        assert fullNode.keys.size() >= t - 1 && newNode.keys.size() >= t - 1;
    }


    private List<User> getAllUsers(BNode node) {
        List<User> allUsers = new ArrayList<>();
        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) {
                allUsers.addAll(getAllUsers(node.children.get(i)));
            }
            allUsers.add(node.keys.get(i));
        }
        if (!node.leaf) {
            allUsers.addAll(getAllUsers(node.children.get(node.keys.size())));
        }
        return allUsers;
    }

    private List<User> readUsersFromFile() {
        List<User> users = new ArrayList<>();
        if (!new File(FILE_PATH).exists()) {
            File file = new File(FILE_PATH);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    users.add(new User(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void appendUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.getId() + "," + user.getEmail() + "," + user.getUsername() + "," + user.getPassword());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeUsersToFile(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.write(user.getId() + "," + user.getEmail() + "," + user.getUsername() + "," + user.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getUserCount() {
        return getUserCount(root);
    }

    private int getUserCount(BNode node) {
        if (node == null) {
            return 0;
        }

        int count = node.keys.size();

        for (BNode child : node.children) {
            count += getUserCount(child);
        }

        return count;
    }

    public List<User> getUsersForPage(int pageIndex, int rowsPerPage) {
        List<User> users = new ArrayList<>();
        int start = pageIndex * rowsPerPage;
        int end = start + rowsPerPage;

        collectUsersForPage(root, users, start, end, new int[]{0});
        return users;
    }

    private void collectUsersForPage(BNode node, List<User> users, int start, int end, int[] count) {
        if (node == null || count[0] >= end) {
            return;
        }

        for (int i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) {
                collectUsersForPage(node.children.get(i), users, start, end, count);
            }

            if (count[0] >= start && count[0] < end) {
                users.add(node.keys.get(i));
            }
            count[0]++;

            if (count[0] >= end) {
                return;
            }
        }

        if (!node.leaf) {
            collectUsersForPage(node.children.get(node.keys.size()), users, start, end, count);
        }
    }

    public int getComparisonCount() {
        int count = numberOfLastComparisons;
        numberOfLastComparisons = 0;
        return count;
    }

    private static class BNode {
        int t;
        List<User> keys;
        List<BNode> children;
        boolean leaf;

        BNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }
    }

}
