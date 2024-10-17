import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicLock {

    public static void main(String[] args) {
        Account fromAccount = new Account(new BigDecimal("90"));
        Account toAccount = new Account(new BigDecimal("90"));
        new Thread(() -> {
            try {
                transferMoney(fromAccount, toAccount, new BigDecimal("10"));
            } catch (InSufficientFundsException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                transferMoney(toAccount, fromAccount, new BigDecimal("20"));
            } catch (InSufficientFundsException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void deadlockTransferMoney(Account fromAccount,
                                             Account toAccount,
                                             BigDecimal amount) throws InSufficientFundsException, InterruptedException {
        synchronized (fromAccount) {
            System.out.println(Thread.currentThread().getName() + " fromAccount acquired");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName() + " woke up!");
            synchronized (toAccount) {
                System.out.println(Thread.currentThread().getName() + " toAccount acquired");
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InSufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
        System.out.println(Thread.currentThread().getName() + " lock released");
    }

    private static final ReentrantLock tieLock = new ReentrantLock();
    public static void transferMoney(final Account fromAccount,
                                     final Account toAccount,
                                     final BigDecimal amount) throws InSufficientFundsException, InterruptedException {
        class Helper {
            public void transfer() throws InSufficientFundsException {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InSufficientFundsException();
                } else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }

        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                System.out.println(Thread.currentThread().getName() + fromHash + "  acquired");
                Thread.sleep(3000);
                synchronized (toAccount) {
                    System.out.println(Thread.currentThread().getName() + toHash + "  acquired");
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                System.out.println(Thread.currentThread().getName() + toHash + "  acquired");
                Thread.sleep(3000);
                synchronized (fromAccount) {
                    System.out.println(Thread.currentThread().getName() + fromHash + "  acquired");
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }

    public static class Account {

        private BigDecimal balance;

        public Account(BigDecimal balance) {
            this.balance = balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void debit(BigDecimal amount) {
            this.balance = balance.subtract(amount);
        }

        public void credit(BigDecimal amount) {
            this.balance = balance.add(amount);
        }
    }

    public static class InSufficientFundsException extends Exception {
    }
}
